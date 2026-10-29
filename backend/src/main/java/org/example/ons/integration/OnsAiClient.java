package org.example.ons.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class OnsAiClient {

    private final String baseUrl;
    private final RestTemplate restTemplate;

    public OnsAiClient(
            @Value("${ons.ai.base-url:http://localhost:8000}") String baseUrl) {
        this.baseUrl = baseUrl.replaceAll("/+$", "");
        this.restTemplate = new RestTemplate();
    }

    public Map<String, Object> scanQr(byte[] imageBytes, String filename) {
        return sendImage("/scan/qr", imageBytes, filename);
    }

    public Map<String, Object> scanOcr(byte[] imageBytes, String filename) {
        return sendImage("/scan/ocr", imageBytes, filename);
    }

    private Map<String, Object> sendImage(
            String endpoint,
            byte[] imageBytes,
            String filename) {

        if (imageBytes == null || imageBytes.length == 0) {
            throw new IllegalArgumentException("Image must not be empty.");
        }

        String safeFilename = (filename == null || filename.isBlank())
                ? "image.jpg"
                : filename.replaceAll("[\\\\/\\r\\n\"]", "_");

        ByteArrayResource resource = new ByteArrayResource(imageBytes) {
            @Override
            public String getFilename() {
                return safeFilename;
            }
        };

        HttpHeaders fileHeaders = new HttpHeaders();
        fileHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        fileHeaders.setContentDispositionFormData("file", safeFilename);

        HttpEntity<ByteArrayResource> fileEntity =
                new HttpEntity<>(resource, fileHeaders);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileEntity);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        requestHeaders.setAccept(
                java.util.List.of(MediaType.APPLICATION_JSON)
        );

        HttpEntity<MultiValueMap<String, Object>> request =
                new HttpEntity<>(body, requestHeaders);

        return restTemplate.postForObject(
                baseUrl + endpoint,
                request,
                Map.class
        );
    }
}
