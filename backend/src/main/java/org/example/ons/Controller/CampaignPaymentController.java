package org.example.ons.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.ons.Api.ApiResponse;
import org.example.ons.Service.CampaignPaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class CampaignPaymentController {

    private final CampaignPaymentService campaignPaymentService;

    @PostMapping("/campaign/{campaignId}")
    public ResponseEntity<?> createPayment(
            @PathVariable Integer campaignId,
            HttpServletRequest request) {

        if (!ownsRegistration(request, campaignId)) {
            return ResponseEntity.status(403).body(
                    new ApiResponse(
                            "Register this campaign in the same session first"
                    )
            );
        }

        return ResponseEntity.status(200)
                .body(
                        campaignPaymentService.createPayment(campaignId)
                );
    }

    @PostMapping("/campaign/{campaignId}/{paymentId}/verify")
    public ResponseEntity<?> verifyPayment(
            @PathVariable Integer campaignId,
            @PathVariable Long paymentId,
            HttpServletRequest request) {

        if (!ownsRegistration(request, campaignId)) {
            return ResponseEntity.status(403).body(
                    new ApiResponse(
                            "Register this campaign in the same session first"
                    )
            );
        }

        return ResponseEntity.status(200)
                .body(
                        campaignPaymentService.verifyPayment(
                                campaignId,
                                paymentId
                        )
                );
    }

    private boolean ownsRegistration(
            HttpServletRequest request,
            Integer campaignId) {

        HttpSession session = request.getSession(false);

        return session != null
                && campaignId.equals(
                session.getAttribute("registrationCampaignId")
        );
    }
}