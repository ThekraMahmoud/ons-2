package org.example.ons.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.ons.Api.ApiException;
import org.example.ons.DTO.MoyasarInvoiceDTO;
import org.example.ons.Enum.CampaignPaymentStatus;
import org.example.ons.Model.Campaign;
import org.example.ons.Model.CampaignPayment;
import org.example.ons.Model.User;
import org.example.ons.Repository.AuthRepository;
import org.example.ons.Repository.CampaignPaymentRepository;
import org.example.ons.Repository.CampaignRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CampaignPaymentService {

    private final CampaignRepository campaignRepository;
    private final CampaignPaymentRepository campaignPaymentRepository;
    private final MoyasarService moyasarService;
    private final NotificationService notificationService;
    private final AuthRepository authRepository;

    @Value("${moyasar.registration-fee-halalas}")
    private Long registrationFee;

    @Transactional
    public CampaignPayment createPayment(Integer campaignId) {

        if (registrationFee == null || registrationFee < 100) {
            throw new ApiException(
                    "Configure a registration fee of at least 100 halalas"
            );
        }

        Campaign campaign = campaignRepository
                .findCampaignForPayment(campaignId)
                .orElseThrow(
                        () -> new ApiException("Campaign not found")
                );

        boolean alreadyPaid =
                campaignPaymentRepository.existsByCampaign_IdAndStatus(
                        campaignId,
                        CampaignPaymentStatus.PAID
                );

        if (alreadyPaid) {
            throw new ApiException(
                    "Campaign registration is already paid"
            );
        }

        CampaignPayment latestPayment =
                campaignPaymentRepository
                        .findTopByCampaign_IdOrderByCreatedAtDescIdDesc(
                                campaignId
                        )
                        .orElse(null);

        if (latestPayment != null &&
                latestPayment.getStatus()
                        == CampaignPaymentStatus.PENDING) {

            if (latestPayment.getInvoiceId() == null ||
                    latestPayment.getPaymentUrl() == null) {
                throw new ApiException(
                        "Existing payment is missing its invoice details"
                );
            }

            return latestPayment;
        }

        MoyasarInvoiceDTO invoice = moyasarService.createInvoice(
                campaignId,
                registrationFee
        );

        if (!Objects.equals(invoice.getAmount(), registrationFee) ||
                !"SAR".equals(invoice.getCurrency()) ||
                !"initiated".equals(invoice.getStatus())) {

            throw new ApiException(
                    "Unexpected invoice details from Moyasar"
            );
        }

        CampaignPayment payment = new CampaignPayment();

        payment.setCampaign(campaign);
        payment.setAmount(registrationFee);
        payment.setCurrency("SAR");
        payment.setInvoiceId(invoice.getId());
        payment.setPaymentUrl(invoice.getUrl());
        payment.setStatus(CampaignPaymentStatus.PENDING);

        return campaignPaymentRepository.save(payment);
    }

    @Transactional
    public CampaignPayment verifyPayment(
            Integer campaignId,
            Long paymentId) {

        Campaign campaign = campaignRepository
                .findCampaignForPayment(campaignId)
                .orElseThrow(
                        () -> new ApiException("Campaign not found")
                );

        CampaignPayment payment = campaignPaymentRepository
                .findById(paymentId)
                .orElseThrow(
                        () -> new ApiException("Payment not found")
                );

        if (!payment.getCampaign().getId().equals(campaign.getId())) {
            throw new ApiException(
                    "Payment does not belong to this campaign"
            );
        }

        if (payment.getInvoiceId() == null) {
            throw new ApiException("Payment has no Moyasar invoice");
        }

        MoyasarInvoiceDTO invoice =
                moyasarService.getInvoice(payment.getInvoiceId());

        if (!Objects.equals(invoice.getAmount(), payment.getAmount()) ||
                !Objects.equals(
                        invoice.getCurrency(),
                        payment.getCurrency()
                )) {

            throw new ApiException(
                    "Moyasar invoice amount or currency does not match"
            );
        }

        if (invoice.getStatus() == null) {
            throw new ApiException("Moyasar invoice status is missing");
        }

        CampaignPaymentStatus status;

        switch (invoice.getStatus()) {

            case "paid":
                status = CampaignPaymentStatus.PAID;
                break;

            case "initiated":
            case "on_hold":
                status = CampaignPaymentStatus.PENDING;
                break;

            case "failed":
                status = CampaignPaymentStatus.FAILED;
                break;

            case "expired":
                status = CampaignPaymentStatus.EXPIRED;
                break;

            case "canceled":
            case "voided":
                status = CampaignPaymentStatus.CANCELED;
                break;


            default:
                throw new ApiException(
                        "Unsupported Moyasar invoice status"
                );
        }


        boolean firstSuccessfulVerification =
                status == CampaignPaymentStatus.PAID
                        && payment.getPaidAt() == null;

        payment.setStatus(status);

        if (status == CampaignPaymentStatus.PAID) {

            if (payment.getPaidAt() == null) {
                payment.setPaidAt(LocalDateTime.now());
            }

            if ("PLANNED".equals(campaign.getStatus())) {
                campaign.setStatus("ACTIVE");
                campaignRepository.save(campaign);
            }
            User owner = campaign.getUser();
            if (owner == null) {
                throw new ApiException("Campaign owner not found");
            }

            owner.setActive(true);
            authRepository.save(owner);
        }

        CampaignPayment savedPayment =
                campaignPaymentRepository.save(payment);

        if (firstSuccessfulVerification) {

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {

                        @Override
                        public void afterCommit() {
                            try {
                                notificationService.notifyPaymentSuccessful(
                                        savedPayment
                                );
                            } catch (RuntimeException exception) {
                                log.error(
                                        "Payment {} was confirmed, but invoice email submission failed",
                                        savedPayment.getId(),
                                        exception
                                );
                            }
                        }
                    }
            );
        }

        return savedPayment;
    }
}