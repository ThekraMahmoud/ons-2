package org.example.ons.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SelfServiceReportDTO {

    @NotBlank(message = "Card number is required")
    private String cardNumber;

    @NotNull(message = "Kiosk ID is required")
    private Integer kioskId;
}
