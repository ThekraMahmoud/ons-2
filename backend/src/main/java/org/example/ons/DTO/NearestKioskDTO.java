package org.example.ons.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NearestKioskDTO {

    private MapMarkerDTO kiosk;
    private long distanceMeters;
    private String duration;
    private List<String> warnings;
}