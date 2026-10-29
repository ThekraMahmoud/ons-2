package org.example.ons.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NearestPlaceDTO {

    private Integer kioskId;
    private String type;
    private String placeId;
    private String name;
    private Double latitude;
    private Double longitude;
    private GoogleRoutesDTO.Route route;
}