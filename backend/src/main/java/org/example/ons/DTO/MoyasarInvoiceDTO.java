package org.example.ons.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MoyasarInvoiceDTO {

    private String id;

    private String status;

    private Long amount;

    private String currency;

    private String url;

    private Map<String, String> metadata;
}