package org.example.ons.Service;

import lombok.AllArgsConstructor;
import org.example.ons.Api.ApiException;
import org.example.ons.Model.Pilgrim;
import org.springframework.stereotype.Service;
import org.example.ons.Model.IdentificationRequest;
import org.example.ons.Enum.IdentificationStatus;
import org.example.ons.Repository.IdentificationRequestRepository;


import java.util.Map;

@Service
@AllArgsConstructor
public class IdentificationService {

    private final PilgrimCardService pilgrimCardService;
    private final IdentificationRequestRepository requestRepository;

    // Identify pilgrim using the QR card number
    public Map<String, Object> scanQr(String cardNumber) {

        if (cardNumber == null || cardNumber.isBlank()) {
            throw new ApiException("Card number is required");
        }

        Pilgrim pilgrim =
                pilgrimCardService.identifyPilgrimByCardNumber(
                        cardNumber.trim()
                );

        return Map.of(
                "message", "Pilgrim identified successfully",
                "pilgrimId", pilgrim.getId(),
                "fullName", pilgrim.getFullName(),
                "visaNumber", pilgrim.getVisaNumber()
        );
    }

    // Validate pilgrim identity using card number and visa number
    public Map<String, Object> validateIdentity(
            org.example.ons.DTO.VerifyPilgrimDTO dto) {

        Pilgrim pilgrim =
                pilgrimCardService.verifyPilgrimIdentity(dto);

        return Map.of(
                "message", "Pilgrim identity validated successfully",
                "pilgrimId", pilgrim.getId(),
                "fullName", pilgrim.getFullName(),
                "visaNumber", pilgrim.getVisaNumber()
        );
    }

    public IdentificationRequest createRequest(String cardNumber) {

        if (cardNumber == null || cardNumber.isBlank()) {
            throw new ApiException("Card number is required");
        }

        IdentificationRequest request = new IdentificationRequest();
        request.setCardNumber(cardNumber.trim());
        request.setStatus(IdentificationStatus.PENDING);

        try {
            pilgrimCardService.identifyPilgrimByCardNumber(
                    request.getCardNumber()
            );

            request.setStatus(IdentificationStatus.COMPLETED);

        } catch (ApiException exception) {
            request.setStatus(IdentificationStatus.FAILED);
            request.setFailureReason(exception.getMessage());
        }

        return requestRepository.save(request);
    }

}
