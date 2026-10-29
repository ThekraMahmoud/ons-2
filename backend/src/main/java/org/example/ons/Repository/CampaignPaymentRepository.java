package org.example.ons.Repository;

import org.example.ons.Model.CampaignPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CampaignPaymentRepository
        extends JpaRepository<CampaignPayment, Long> {

    Optional<CampaignPayment> findByInvoiceId(String invoiceId);

    Optional<CampaignPayment> findTopByCampaign_IdOrderByCreatedAtDescIdDesc(
            Integer campaignId
    );

    boolean existsByCampaign_IdAndStatus(
            Integer campaignId,
            org.example.ons.Enum.CampaignPaymentStatus status
    );
}