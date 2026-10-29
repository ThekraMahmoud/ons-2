
package org.example.ons.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RouteService {

    private final RestTemplate restTemplate = new RestTemplate();

    public String getRoute(
            double startLon,
            double startLat,
            double endLon,
            double endLat) {

        String url = String.format(
                java.util.Locale.US,
                "https://router.project-osrm.org/route/v1/driving/%.6f,%.6f;%.6f,%.6f?overview=full&geometries=geojson",
                startLon, startLat,
                endLon, endLat
        );

        return restTemplate.getForObject(url, String.class);
    }
}
