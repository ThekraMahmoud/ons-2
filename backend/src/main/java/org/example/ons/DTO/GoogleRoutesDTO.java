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
public class GoogleRoutesDTO {

    private List<Route> routes;

    @Getter
    @Setter
    public static class Route {

        private long distanceMeters;
        private String duration;
        private List<String> warnings;
        private Polyline polyline;
    }

    @Getter
    @Setter
    public static class Polyline {

        private String encodedPolyline;
    }
}