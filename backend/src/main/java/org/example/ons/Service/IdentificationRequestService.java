package org.example.ons.Service;

import lombok.AllArgsConstructor;
import org.example.ons.Api.ApiException;
import org.example.ons.Model.IdentificationRequest;
import org.example.ons.Enum.IdentificationStatus;
import org.example.ons.Repository.IdentificationRequestRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class IdentificationRequestService {

    private final IdentificationRequestRepository repository;
    private final PilgrimCardService pilgrimCardService;

    public IdentificationRequest getRequestById(Long requestId) {
        return repository.findById(requestId)
                .orElseThrow(() ->
                        new ApiException(
                                "Identification request not found: " + requestId
                        )
                );
    }

    public IdentificationRequest retryRequest(Long requestId) {
        IdentificationRequest request = getRequestById(requestId);

        if (request.getStatus() != IdentificationStatus.FAILED) {
            throw new ApiException(
                    "Only failed identification requests can be retried"
            );
        }

        try {
            pilgrimCardService.identifyPilgrimByCardNumber(
                    request.getCardNumber()
            );

            request.setStatus(IdentificationStatus.COMPLETED);
            request.setFailureReason(null);

        } catch (ApiException exception) {
            request.setStatus(IdentificationStatus.FAILED);
            request.setFailureReason(exception.getMessage());
        }

        return repository.save(request);
    }

    public IdentificationRequest submitForManualReview(Long requestId) {
        IdentificationRequest request = getRequestById(requestId);

        if (request.getStatus() == IdentificationStatus.COMPLETED) {
            throw new ApiException(
                    "Completed requests cannot be sent for manual review"
            );
        }

        request.setStatus(IdentificationStatus.MANUAL_REVIEW);

        return repository.save(request);
    }
}
