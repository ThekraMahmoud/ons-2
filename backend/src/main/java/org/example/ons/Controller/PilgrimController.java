        package org.example.ons.Controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.ons.Api.ApiResponse;
import org.example.ons.Model.Pilgrim;
import org.example.ons.Service.PilgrimService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pilgrims")
@AllArgsConstructor
public class PilgrimController {

    private final PilgrimService pilgrimService;

    // Create Pilgrim
    @PostMapping("/add/{campaignId}")
    public ResponseEntity<?> createPilgrim(@PathVariable int campaignId ,@Valid @RequestBody Pilgrim pilgrim) {

        pilgrimService.createPilgrim(campaignId,pilgrim);

        return ResponseEntity.status(200).body(new ApiResponse("Pilgrim created successfully"));
    }

    // Get All Pilgrims
    @GetMapping("/get")
    public ResponseEntity<?> getAllPilgrims() {

        return ResponseEntity.status(200).body(pilgrimService.getAllPilgrims());
    }

    // Get Pilgrim By ID
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getPilgrimById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(pilgrimService.getPilgrimById(id));
    }

    // Update Pilgrim
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePilgrim(@PathVariable Integer id, @Valid @RequestBody Pilgrim pilgrim) {
        pilgrimService.updatePilgrim(id, pilgrim);

        return ResponseEntity.status(200).body(new ApiResponse("Pilgrim updated successfully"));
    }

    // Delete Pilgrim
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePilgrim(@PathVariable Integer id) {

        pilgrimService.deletePilgrim(id);
        return ResponseEntity.status(200).body(new ApiResponse("Pilgrim deleted successfully"));
    }
}