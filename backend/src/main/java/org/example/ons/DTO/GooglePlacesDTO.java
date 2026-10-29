package org.example.ons.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GooglePlacesDTO {

    private List<Place> places;

    @Getter
    @Setter
    public static class Place {

        private String id;
        private DisplayName displayName;
        private Location location;
    }

    @Getter
    @Setter
    public static class DisplayName {

        private String text;
    }

    @Getter
    @Setter
    public static class Location {

        private Double latitude;
        private Double longitude;
    }
}