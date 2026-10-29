
package org.example.ons.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PilgrimResponseDTO {


    private String fullName;
    private String nationality;
    private String phoneNumber;
    private String visaNumber;

    private Integer campaignId;
    private String campaignName;
}
