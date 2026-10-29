package org.example.ons.Service;

import lombok.RequiredArgsConstructor;
import org.example.ons.Api.ApiException;
import org.example.ons.DTO.*;
import org.example.ons.Model.Kiosk;
import org.example.ons.Repository.KioskRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MapService {

    private final KioskRepository kioskRepository;

    private final RestClient restClient = RestClient.create();

    @Value("${google.maps.api-key}")
    private String googleMapsApiKey;

    public List<MapMarkerDTO> getMarkers() {

        List<Kiosk> kiosks = kioskRepository.findByStatus("ACTIVE");

        List<MapMarkerDTO> markers = new ArrayList<>();

        for (Kiosk kiosk : kiosks) {
            MapMarkerDTO marker = new MapMarkerDTO();

            marker.setKioskId(kiosk.getId());
            marker.setKioskCode(kiosk.getKioskCode());
            marker.setLocationName(kiosk.getLocationName());
            marker.setLatitude(kiosk.getLatitude());
            marker.setLongitude(kiosk.getLongitude());

            markers.add(marker);
        }

        return markers;
    }

    public NearestKioskDTO getNearestKiosk(
            Double latitude,
            Double longitude) {

        if (!validCoordinates(latitude, longitude)) {
            throw new ApiException("Invalid latitude or longitude");
        }

        List<MapMarkerDTO> markers = getMarkers();

        NearestKioskDTO nearest = null;

        for (MapMarkerDTO marker : markers) {

            if (!validCoordinates(
                    marker.getLatitude(),
                    marker.getLongitude())) {
                continue;
            }

            GoogleRoutesDTO response = getWalkingRoutes(
                    latitude,
                    longitude,
                    marker.getLatitude(),
                    marker.getLongitude()
            );

            if (response.getRoutes() == null ||
                    response.getRoutes().isEmpty()) {
                continue;
            }

            for (GoogleRoutesDTO.Route route : response.getRoutes()) {

                if (route == null || route.getDuration() == null) {
                    throw new ApiException("Invalid Google Routes response");
                }

                if (nearest == null ||
                        route.getDistanceMeters()
                                < nearest.getDistanceMeters()) {

                    nearest = new NearestKioskDTO();

                    nearest.setKiosk(marker);
                    nearest.setDistanceMeters(route.getDistanceMeters());
                    nearest.setDuration(route.getDuration());
                    nearest.setWarnings(
                            route.getWarnings() == null
                                    ? List.of()
                                    : route.getWarnings()
                    );
                }
            }
        }

        if (nearest == null) {
            throw new ApiException(
                    "No reachable active kiosk with valid coordinates"
            );
        }

        return nearest;
    }

    public GoogleRoutesDTO getWalkingRoutes(
            double originLatitude,
            double originLongitude,
            double destinationLatitude,
            double destinationLongitude) {

        Map<String, Object> body = Map.of(
                "origin", waypoint(originLatitude, originLongitude),
                "destination", waypoint(
                        destinationLatitude,
                        destinationLongitude
                ),
                "travelMode", "WALK",
                "computeAlternativeRoutes", true,
                "languageCode", "ar"
        );

        try {
            GoogleRoutesDTO response = restClient.post()
                    .uri(
                            "https://routes.googleapis.com/"
                                    + "directions/v2:computeRoutes"
                    )
                    .header("X-Goog-Api-Key", googleMapsApiKey)
                    .header(
                            "X-Goog-FieldMask",
                            "routes.distanceMeters,routes.duration,"
                                    + "routes.warnings,"
                                    + "routes.polyline.encodedPolyline"
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(GoogleRoutesDTO.class);

            if (response == null) {
                throw new ApiException("Empty Google Routes response");
            }

            return response;

        } catch (RestClientResponseException e) {
            throw new ApiException(
                    "Google Routes request failed: HTTP "
                            + e.getStatusCode().value()
            );

        } catch (RestClientException e) {
            throw new ApiException("Could not contact Google Routes");
        }
    }

    private Map<String, Object> waypoint(
            double latitude,
            double longitude) {

        return Map.of(
                "location", Map.of(
                        "latLng", Map.of(
                                "latitude", latitude,
                                "longitude", longitude
                        )
                )
        );
    }

    public boolean validCoordinates(
            Double latitude,
            Double longitude) {

        return latitude != null
                && longitude != null
                && Double.isFinite(latitude)
                && Double.isFinite(longitude)
                && latitude >= -90 && latitude <= 90
                && longitude >= -180 && longitude <= 180;
    }

    public GoogleRoutesDTO.Route getRoute(RouteRequestDTO dto) {

        if (!validCoordinates(
                dto.getOriginLatitude(),
                dto.getOriginLongitude()) ||
                !validCoordinates(
                        dto.getDestinationLatitude(),
                        dto.getDestinationLongitude())) {

            throw new ApiException("Invalid latitude or longitude");
        }

        GoogleRoutesDTO response = getWalkingRoutes(
                dto.getOriginLatitude(),
                dto.getOriginLongitude(),
                dto.getDestinationLatitude(),
                dto.getDestinationLongitude()
        );

        if (response.getRoutes() == null ||
                response.getRoutes().isEmpty()) {
            throw new ApiException("No walking route found");
        }

        GoogleRoutesDTO.Route shortest = null;

        for (GoogleRoutesDTO.Route route : response.getRoutes()) {

            if (route == null || route.getDuration() == null) {
                throw new ApiException("Invalid Google Routes response");
            }

            if (shortest == null ||
                    route.getDistanceMeters()
                            < shortest.getDistanceMeters()) {
                shortest = route;
            }
        }

        return shortest;
    }

    private GooglePlacesDTO searchNearbyPlaces(
            Kiosk kiosk,
            String type) {

        Map<String, Object> circle = Map.of(
                "circle", Map.of(
                        "center", Map.of(
                                "latitude", kiosk.getLatitude(),
                                "longitude", kiosk.getLongitude()
                        ),
                        "radius", 2000.0
                )
        );

        String url;
        Map<String, Object> body;

        if ("GATE".equals(type)) {
            url = "https://places.googleapis.com/v1/places:searchText";

            body = Map.of(
                    "textQuery", "بوابات المسجد الحرام",
                    "pageSize", 10,
                    "languageCode", "ar",
                    "locationBias", circle
            );

        } else {
            url = "https://places.googleapis.com/v1/places:searchNearby";

            body = Map.of(
                    "includedTypes", List.of("public_bathroom"),
                    "maxResultCount", 10,
                    "rankPreference", "DISTANCE",
                    "languageCode", "ar",
                    "locationRestriction", circle
            );
        }

        try {
            GooglePlacesDTO response = restClient.post()
                    .uri(url)
                    .header("X-Goog-Api-Key", googleMapsApiKey)
                    .header(
                            "X-Goog-FieldMask",
                            "places.id,places.displayName,places.location"
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(GooglePlacesDTO.class);

            if (response == null) {
                throw new ApiException("Empty Google Places response");
            }

            return response;

        } catch (RestClientResponseException e) {
            throw new ApiException(
                    "Google Places request failed: HTTP "
                            + e.getStatusCode().value()
            );

        } catch (RestClientException e) {
            throw new ApiException("Could not contact Google Places");
        }
    }

    public NearestPlaceDTO getNearestPlace(
            Integer kioskId,
            String type) {

        String requestedType = type == null
                ? ""
                : type.trim().toUpperCase(Locale.ROOT);

        if (!requestedType.equals("BATHROOM") &&
                !requestedType.equals("GATE")) {
            throw new ApiException(
                    "Supported types are BATHROOM and GATE"
            );
        }

        Kiosk kiosk = kioskRepository.findKioskById(kioskId);

        if (kiosk == null) {
            throw new ApiException("Kiosk not found");
        }

        if (!"ACTIVE".equals(kiosk.getStatus())) {
            throw new ApiException("Kiosk is inactive");
        }

        if (!validCoordinates(
                kiosk.getLatitude(),
                kiosk.getLongitude())) {
            throw new ApiException("Kiosk has invalid coordinates");
        }

        GooglePlacesDTO response =
                searchNearbyPlaces(kiosk, requestedType);

        if (response.getPlaces() == null ||
                response.getPlaces().isEmpty()) {
            throw new ApiException("No matching places found");
        }

        NearestPlaceDTO nearest = null;

        for (GooglePlacesDTO.Place place : response.getPlaces()) {

            if (place == null ||
                    place.getId() == null ||
                    place.getDisplayName() == null ||
                    place.getDisplayName().getText() == null ||
                    place.getDisplayName().getText().isBlank() ||
                    place.getLocation() == null ||
                    !validCoordinates(
                            place.getLocation().getLatitude(),
                            place.getLocation().getLongitude())) {
                continue;
            }

            String name = place.getDisplayName().getText();

            if (requestedType.equals("GATE")) {
                String normalized = name.toLowerCase(Locale.ROOT);

                if (!normalized.contains("باب ") &&
                        !normalized.contains("بوابة") &&
                        !normalized.matches(".*\\bgates?\\b.*")) {
                    continue;
                }
            }

            GoogleRoutesDTO routesResponse = getWalkingRoutes(
                    kiosk.getLatitude(),
                    kiosk.getLongitude(),
                    place.getLocation().getLatitude(),
                    place.getLocation().getLongitude()
            );

            if (routesResponse.getRoutes() == null ||
                    routesResponse.getRoutes().isEmpty()) {
                continue;
            }

            for (GoogleRoutesDTO.Route route
                    : routesResponse.getRoutes()) {

                if (route == null || route.getDuration() == null) {
                    throw new ApiException("Invalid Google Routes response");
                }

                if (nearest == null ||
                        route.getDistanceMeters()
                                < nearest.getRoute().getDistanceMeters()) {

                    nearest = new NearestPlaceDTO();

                    nearest.setKioskId(kiosk.getId());
                    nearest.setType(requestedType);
                    nearest.setPlaceId(place.getId());
                    nearest.setName(name);
                    nearest.setLatitude(
                            place.getLocation().getLatitude()
                    );
                    nearest.setLongitude(
                            place.getLocation().getLongitude()
                    );
                    nearest.setRoute(route);
                }
            }
        }

        if (nearest == null) {
            throw new ApiException("No reachable matching place found");
        }

        return nearest;
    }
}