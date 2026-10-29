
package org.example.ons.Controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.ons.Api.ApiResponse;
import org.example.ons.Model.Accommodation;
import org.example.ons.Service.AccommodationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accommodations")
@AllArgsConstructor
public class AccommodationController {

    private final AccommodationService accommodationService;

    // Create Accommodation
    @PostMapping("/add")
    public ResponseEntity<?> addAccommodation(@Valid @RequestBody Accommodation accommodation) {
        accommodationService.createAccommodation(accommodation);
        return ResponseEntity.status(200).body(new ApiResponse("Accommodation added successfully"));
    }

    // Get All Accommodations
    @GetMapping("/get")
    public ResponseEntity<?> getAllAccommodations() {
        return ResponseEntity.status(200).body(accommodationService.getAllAccommodations());
    }

    // Get Accommodation By ID
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getAccommodationById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(accommodationService.getAccommodationById(id));
    }

    // Update Accommodation
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAccommodation(@PathVariable Integer id, @Valid @RequestBody Accommodation accommodation) {
        accommodationService.updateAccommodation(id, accommodation);
        return ResponseEntity.status(200).body(new ApiResponse("Accommodation updated successfully"));
    }

    // Delete Accommodation
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAccommodation(@PathVariable Integer id) {
        accommodationService.deleteAccommodation(id);
        return ResponseEntity.status(200).body(new ApiResponse("Accommodation deleted successfully"));
    }
}
