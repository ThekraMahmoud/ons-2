package org.example.ons.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ons.DTO.RouteRequestDTO;
import org.example.ons.Service.MapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/map")
@RequiredArgsConstructor
public class MapController {

    private final MapService mapService;

    @GetMapping("/markers")
    public ResponseEntity<?> getMarkers() {
        return ResponseEntity.status(200)
                .body(mapService.getMarkers());
    }

    @GetMapping("/nearest-kiosk")
    public ResponseEntity<?> getNearestKiosk(
            @RequestParam Double latitude,
            @RequestParam Double longitude) {

        return ResponseEntity.status(200)
                .body(mapService.getNearestKiosk(
                        latitude,
                        longitude
                ));
    }

    @PostMapping("/route")
    public ResponseEntity<?> getRoute(
            @Valid @RequestBody RouteRequestDTO dto) {

        return ResponseEntity.status(200)
                .body(mapService.getRoute(dto));
    }

    @GetMapping("/kiosks/{kioskId}/nearest-place")
    public ResponseEntity<?> getNearestPlace(
            @PathVariable Integer kioskId,
            @RequestParam String type) {

        return ResponseEntity.status(200)
                .body(mapService.getNearestPlace(
                        kioskId,
                        type
                ));
    }
}