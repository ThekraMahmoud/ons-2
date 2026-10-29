package org.example.ons.Service;

import org.example.ons.Api.ApiException;
import org.example.ons.DTO.MoyasarInvoiceDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Service
public class MoyasarService {

    @Value("${moyasar.secret-key}")
    private String secretKey;

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.moyasar.com/v1")
            .build();

    public MoyasarInvoiceDTO createInvoice(
            Integer campaignId,
            Long amount) {

        checkSecretKey();

        if (campaignId == null) {
            throw new ApiException("Campaign ID is required");
        }

        if (amount == null || amount < 100) {
            throw new ApiException(
                    "Payment amount must be at least 100 halalas"
            );
        }

        var body = java.util.Map.of(
                "amount", amount,
                "currency", "SAR",
                "description",
                "ONS campaign registration #" + campaignId
        );

        try {
            MoyasarInvoiceDTO invoice = restClient.post()
                    .uri("/invoices")
                    .headers(headers ->
                            headers.setBasicAuth(secretKey, "")
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(MoyasarInvoiceDTO.class);

            if (invoice == null ||
                    invoice.getId() == null ||
                    invoice.getUrl() == null) {

                throw new ApiException(
                        "Invalid invoice response from Moyasar"
                );
            }

            return invoice;

        } catch (RestClientResponseException e) {
            throw new ApiException(
                    "Moyasar invoice creation failed: HTTP "
                            + e.getStatusCode().value()
            );

        } catch (RestClientException e) {
            throw new ApiException("Could not contact Moyasar");
        }
    }

    public MoyasarInvoiceDTO getInvoice(String invoiceId) {

        checkSecretKey();

        if (invoiceId == null || invoiceId.isBlank()) {
            throw new ApiException("Invoice ID is required");
        }

        try {
            MoyasarInvoiceDTO invoice = restClient.get()
                    .uri("/invoices/{invoiceId}", invoiceId)
                    .headers(headers ->
                            headers.setBasicAuth(secretKey, "")
                    )
                    .retrieve()
                    .body(MoyasarInvoiceDTO.class);

            if (invoice == null ||
                    !invoiceId.equals(invoice.getId())) {

                throw new ApiException(
                        "Invalid invoice response from Moyasar"
                );
            }

            return invoice;

        } catch (RestClientResponseException e) {
            throw new ApiException(
                    "Moyasar invoice retrieval failed: HTTP "
                            + e.getStatusCode().value()
            );

        } catch (RestClientException e) {
            throw new ApiException("Could not contact Moyasar");
        }
    }

    private void checkSecretKey() {

        if (secretKey == null || secretKey.isBlank()) {
            throw new ApiException(
                    "Moyasar secret key is not configured"
            );
        }
    }
}