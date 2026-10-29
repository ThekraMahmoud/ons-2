package org.example.ons.Controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.ons.Api.ApiResponse;
import org.example.ons.Model.Accommodation;
import org.example.ons.Model.AuthorizedStaff;
import org.example.ons.Model.User;
import org.example.ons.Service.AccommodationService;
import org.example.ons.Service.AuthorizedStaffService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accommodations")
@AllArgsConstructor
@Validated
public class AccommodationController {

    private final AccommodationService accommodationService;
    private final AuthorizedStaffService authorizedStaffService;

    @PostMapping("/add")
    public ResponseEntity<?> addAccommodation(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody Accommodation accommodation) {

        accommodationService.createAccommodation(user, accommodation);

        return ResponseEntity.status(200).body(
                new ApiResponse("Accommodation added successfully")
        );
    }

    @GetMapping("/get")
    public ResponseEntity<?> getAllAccommodations(
            @AuthenticationPrincipal User user) {

        return ResponseEntity.status(200).body(
                accommodationService.getAllAccommodations(user.getId())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAccommodationById(
            @AuthenticationPrincipal User user,
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                accommodationService.getAccommodationById(user, id)
        );
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAccommodation(
            @AuthenticationPrincipal User user,
            @PathVariable Integer id,
            @Valid @RequestBody Accommodation accommodation) {

        accommodationService.updateAccommodation(user, id, accommodation);

        return ResponseEntity.status(200).body(
                new ApiResponse("Accommodation updated successfully")
        );
    }

    @PatchMapping("/{accommodationId}/status")
    public ResponseEntity<?> updateAccommodationStatus(
            @AuthenticationPrincipal User user,
            @PathVariable Integer accommodationId,
            @Valid @RequestBody AccommodationStatusRequest request) {

        accommodationService.updateAccommodationStatus(
                user,
                accommodationId,
                request.getStatus()
        );

        return ResponseEntity.status(200).body(
                new ApiResponse("Accommodation status updated successfully")
        );
    }

    @GetMapping("/{accommodationId}/pilgrims")
    public ResponseEntity<?> getAccommodationPilgrims(
            @PathVariable Integer accommodationId) {

        return ResponseEntity.status(200).body(
                accommodationService.getAccommodationPilgrims(
                        accommodationId
                )
        );
    }

    @GetMapping("/{accommodationId}/reports")
    public ResponseEntity<?> getAccommodationReports(
            @PathVariable Integer accommodationId) {

        return ResponseEntity.status(200).body(
                accommodationService.getAccommodationReports(
                        accommodationId
                )
        );
    }

    @GetMapping("/my-campaign")
    public ResponseEntity<?> getCampaignAccommodationsWithPilgrimsAndReports(
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(
                accommodationService
                        .getCampaignAccommodationsWithPilgrimsAndReports(user)
        );
    }

    @GetMapping("/{accommodationId}/location")
    public ResponseEntity<?> getAccommodationLocation(
            @PathVariable Integer accommodationId) {

        return ResponseEntity.status(200).body(
                accommodationService.getAccommodationLocation(
                        accommodationId
                )
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAccommodation(
            @AuthenticationPrincipal User user,
            @PathVariable Integer id) {

        accommodationService.deleteAccommodation(user, id);

        return ResponseEntity.status(200).body(
                new ApiResponse("Accommodation deleted successfully")
        );
    }


    private Integer getStaffCampaignId(User user) {

        if (user == null
                || !user.isActive()
                || !"AuthorizedStaff".equals(user.getRole())) {

            throw new AccessDeniedException(
                    "Only campaign staff can access this endpoint"
            );
        }

        AuthorizedStaff staff =
                authorizedStaffService.getStaffById(user.getId());

        if (staff.getCampaign() == null) {
            throw new AccessDeniedException(
                    "You are not linked to a campaign"
            );
        }

        return staff.getCampaign().getId();
    }
    @Getter
    @Setter
    @NoArgsConstructor
    public static class AccommodationStatusRequest {

        @NotBlank
        @Pattern(
                regexp = "ACTIVE|INACTIVE",
                message = "Status must be ACTIVE or INACTIVE"
        )
        private String status;
    }
}