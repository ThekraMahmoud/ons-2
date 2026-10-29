package org.example.ons.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyPilgrimDTO {

    @NotBlank(message = "Visa number is required")
    @Size(max = 255)
    private String visaNumber;

    @NotBlank(message = "Card number is required")
    @Size(max = 50)
    private String cardNumber;
}
