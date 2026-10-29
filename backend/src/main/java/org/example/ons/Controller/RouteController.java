
package org.example.ons.Controller;

import org.example.ons.Service.RouteService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping(produces = "application/json")
    public String getRoute(
            @RequestParam double startLon,
            @RequestParam double startLat,
            @RequestParam double endLon,
            @RequestParam double endLat) {

        return routeService.getRoute(
                startLon, startLat,
                endLon, endLat
        );
    }
}
