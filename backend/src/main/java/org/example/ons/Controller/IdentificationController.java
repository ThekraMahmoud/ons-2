package org.example.ons.Controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.example.ons.Service.IdentificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.ons.Model.IdentificationRequest;
import org.example.ons.Service.IdentificationRequestService;
import org.example.ons.integration.OnsAiClient;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/identification")
@AllArgsConstructor
public class IdentificationController {

    private final IdentificationService identificationService;
    private final IdentificationRequestService identificationRequestService;
    private final OnsAiClient onsAiClient;

    // Scan QR - frontend sends the extracted card number
    @PostMapping("/qr/scan")
    public ResponseEntity<?> scanQr(
            @Valid @RequestBody QrScanRequest request) {

        IdentificationRequest identificationRequest =
                identificationService.createRequest(request.getCardNumber());

        return ResponseEntity.ok(Map.of(
                "message", "Identification request created",
                "requestId", identificationRequest.getRequestId(),
                "status", identificationRequest.getStatus().name()
        ));
    }

    public static class QrScanRequest {

        @NotBlank(message = "Card number is required")
        @Size(max = 50, message = "Card number must not exceed 50 characters")
        private String cardNumber;

        public String getCardNumber() {
            return cardNumber;
        }

        public void setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
        }
    }

    // Validate extracted identification data
    @PostMapping("/validate")
    public ResponseEntity<?> validateIdentity(
            @Valid @RequestBody org.example.ons.DTO.VerifyPilgrimDTO dto) {

        return ResponseEntity.ok(
                identificationService.validateIdentity(dto)
        );
    }

    // Get identification request by ID
    @GetMapping("/requests/{requestId}")
    public ResponseEntity<?> getRequest(
            @PathVariable Long requestId) {

        return ResponseEntity.ok(
                identificationRequestService.getRequestById(requestId)
        );
    }

    // Retry failed identification request
    @PostMapping("/requests/{requestId}/retry")
    public ResponseEntity<?> retryRequest(
            @PathVariable Long requestId) {

        return ResponseEntity.ok(
                identificationRequestService.retryRequest(requestId)
        );
    }

    // Submit request for manual review
    @PostMapping("/requests/{requestId}/manual-review")
    public ResponseEntity<?> manualReview(
            @PathVariable Long requestId) {

        return ResponseEntity.ok(
                identificationRequestService.submitForManualReview(requestId)
        );

    }

    @PostMapping(value = "/scan", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> scanCard(@RequestParam("file") MultipartFile file
    ) throws IOException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Please provide a card image.")
            );
        }

        Map<String, Object> ocrResult = onsAiClient.scanOcr(
                file.getBytes(),
                file.getOriginalFilename()
        );

        if (!Boolean.TRUE.equals(ocrResult.get("success"))) {
            return ResponseEntity.unprocessableEntity().body(
                    Map.of(
                            "message", "Could not read the card.",
                            "ocrResult", ocrResult
                    )
            );
        }

        Object textObject = ocrResult.get("text");

        if (!(textObject instanceof String text)) {
            return ResponseEntity.unprocessableEntity().body(
                    Map.of("message", "OCR did not return readable text.")
            );
        }

        String cardNumber = null;

// Find the 15-digit HID number anywhere in OCR text.
        Pattern numberPattern = Pattern.compile("[0-9]{15}");
        Matcher numberMatcher = numberPattern.matcher(text);

        while (numberMatcher.find()) {
            String candidate = numberMatcher.group();

            if (candidate.startsWith("447")) {
                cardNumber = candidate;
                break;
            }
        }

        if (cardNumber == null) {
            return ResponseEntity.unprocessableEntity().body(
                    Map.of(
                            "message", "Could not extract the HID number.",
                            "ocrText", text
                    )
            );
        }


        IdentificationRequest request =
                identificationService.createRequest(cardNumber);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Card scanned and identification request created.",
                        "requestId", request.getRequestId(),
                        "cardNumber", cardNumber,
                        "status", request.getStatus().name()
                )
        );
    }

}
