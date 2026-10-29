package org.example.ons.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MapMarkerDTO {

    private Integer kioskId;
    private String kioskCode;
    private String locationName;
    private Double latitude;
    private Double longitude;
}