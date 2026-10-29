package org.example.ons.Controller;

import org.example.ons.integration.OnsAiClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/ai-test")
public class OnsAiTestController {

    private final OnsAiClient onsAiClient;

    public OnsAiTestController(OnsAiClient onsAiClient) {
        this.onsAiClient = onsAiClient;
    }

    @PostMapping(
            value = "/qr",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> testQr(
            @RequestParam("file") MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Please upload an image."));
        }

        return ResponseEntity.ok(
                onsAiClient.scanQr(file.getBytes(), file.getOriginalFilename())
        );
    }

    @PostMapping(
            value = "/ocr",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> testOcr(
            @RequestParam("file") MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Please upload an image."));
        }

        return ResponseEntity.ok(
                onsAiClient.scanOcr(file.getBytes(), file.getOriginalFilename())
        );
    }
}
