        package org.example.ons.Controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.ons.Api.ApiResponse;
import org.example.ons.Model.PilgrimCard;
import org.example.ons.Service.PilgrimCardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pilgrim-cards")
@AllArgsConstructor
public class PilgrimCardController {

    private final PilgrimCardService pilgrimCardService;

    // Create Pilgrim Card
    @PostMapping("/add/{pilgrimId}")
    public ResponseEntity<?> createPilgrimCard(@PathVariable Integer pilgrimId, @Valid @RequestBody PilgrimCard pilgrimCard) {

        pilgrimCardService.createPilgrimCard(pilgrimId,pilgrimCard);

        return ResponseEntity.status(200).body(new ApiResponse("Pilgrim card created successfully"));
    }

    // Get All Pilgrim Cards
    @GetMapping("/get")
    public ResponseEntity<?> getAllPilgrimCards() {

        return ResponseEntity.status(200).body(pilgrimCardService.getAllPilgrimCards());
    }

    // Get Pilgrim Card By ID
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getPilgrimCardById(@PathVariable Integer id) {

        return ResponseEntity.status(200).body(pilgrimCardService.getPilgrimCardById(id));
    }

    // Update Pilgrim Card
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePilgrimCard(@PathVariable Integer id, @Valid @RequestBody PilgrimCard pilgrimCard) {

        pilgrimCardService.updatePilgrimCard(id, pilgrimCard);

        return ResponseEntity.status(200).body(new ApiResponse("Pilgrim card updated successfully"));
    }

    // Delete Pilgrim Card
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePilgrimCard(@PathVariable Integer id) {

        pilgrimCardService.deletePilgrimCard(id);
        return ResponseEntity.status(200).body(new ApiResponse("Pilgrim card deleted successfully"));
    }
}
