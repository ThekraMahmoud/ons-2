package org.example.ons.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ons.Api.ApiResponse;
import org.example.ons.DTO.AuthorizedStaffDTO;
import org.example.ons.DTO.EmployeeUpdateDTO;
import org.example.ons.Enum.ReportStatus;
import org.example.ons.Model.User;
import org.example.ons.Service.AuthorizedStaffService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/authorized-staff")
@RequiredArgsConstructor
public class AuthorizedStaffController {

    private final AuthorizedStaffService authorizedStaffService;

    @PostMapping("/create/{campaignId}")
    public ResponseEntity<?> createStaff(
            @AuthenticationPrincipal User user,
            @PathVariable Integer campaignId,
            @Valid @RequestBody AuthorizedStaffDTO dto) {

        authorizedStaffService.createStaff(user, dto, campaignId);

        return ResponseEntity.status(200)
                .body(new ApiResponse(
                        "Authorized staff Registered successfully"
                ));
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllStaff(
            @AuthenticationPrincipal User user) {

        return ResponseEntity.status(200)
                .body(authorizedStaffService.getAllStaff(user));
    }
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getStaffById(
            @PathVariable Integer id) {

        return ResponseEntity.status(200)
                .body(authorizedStaffService.getStaffById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateStaff(
            @AuthenticationPrincipal User user,
            @PathVariable Integer id,
            @Valid @RequestBody EmployeeUpdateDTO dto) {

        authorizedStaffService.updateStaff(user, id, dto);

        return ResponseEntity.status(200).body(
                new ApiResponse("Authorized staff updated successfully")
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteStaff(
            @AuthenticationPrincipal User user,
            @PathVariable Integer id) {

        authorizedStaffService.deleteStaff(user, id);

        return ResponseEntity.status(200).body(
                new ApiResponse("Authorized staff deleted successfully")
        );
    }

    @GetMapping("/get/employees")
    public ResponseEntity<?> getEmployees(
            @AuthenticationPrincipal User user) {

        return ResponseEntity.status(200)
                .body(authorizedStaffService.getEmployees(
                        user.getId()
                ));
    }

    @PostMapping("/add/employee")
    public ResponseEntity<?> addEmployee(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AuthorizedStaffDTO dto) {

        authorizedStaffService.addEmployee(user, dto);

        return ResponseEntity.status(200)
                .body(new ApiResponse("Employee added successfully"));
    }

    @PutMapping("/update/employee/{employeeId}")
    public ResponseEntity<?> updateEmployee(
            @AuthenticationPrincipal User user,
            @PathVariable Integer employeeId,
            @Valid @RequestBody EmployeeUpdateDTO dto) {

        authorizedStaffService.updateEmployee(
                user,
                employeeId,
                dto
        );

        return ResponseEntity.status(200)
                .body(new ApiResponse("Employee updated successfully"));
    }

    @DeleteMapping("/delete/employee/{employeeId}")
    public ResponseEntity<?> deleteEmployee(
            @AuthenticationPrincipal User user,
            @PathVariable Integer employeeId) {

        authorizedStaffService.deleteEmployee(
                user,
                employeeId
        );

        return ResponseEntity.status(200)
                .body(new ApiResponse("Employee deleted successfully"));
    }

    @PutMapping("/pilgrims/{pilgrimId}/reports/{reportId}/accept")
    public ResponseEntity<?> acceptPilgrim(
            @AuthenticationPrincipal User user,
            @PathVariable Integer pilgrimId,
            @PathVariable long reportId) {

        authorizedStaffService.acceptPilgrim(
                user.getId(),
                pilgrimId,
                reportId
        );

        return ResponseEntity.status(200)
                .body(new ApiResponse("Pilgrim accepted successfully"));
    }

    @PutMapping("/reports/{reportId}/advance-status")
    public ResponseEntity<?> advanceReportStatus(
            @AuthenticationPrincipal User user,
            @PathVariable long reportId) {

        authorizedStaffService.advanceReportStatus(
                user.getId(),
                reportId
        );

        return ResponseEntity.status(200)
                .body(new ApiResponse(
                        "Report status updated successfully"
                ));
    }

    @GetMapping("/staff")
    public ResponseEntity<?> getStaffReports(
            @AuthenticationPrincipal User user) {

        return ResponseEntity.status(200)
                .body(authorizedStaffService.getStaffReports(
                        user.getId()
                ));
    }

    @GetMapping("/staff/status/{status}")
    public ResponseEntity<?> getStaffReportsByStatus(
            @AuthenticationPrincipal User user,
            @PathVariable ReportStatus status) {

        return ResponseEntity.status(200)
                .body(authorizedStaffService.getStaffReportsByStatus(
                        user.getId(),
                        status
                ));
    }

    @GetMapping("/nearby-reports")
    public ResponseEntity<?> getNearbyReports(
            @AuthenticationPrincipal User user,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {

        return ResponseEntity.status(200)
                .body(authorizedStaffService.getNearbyReports(
                        user.getId(),
                        latitude,
                        longitude
                ));
    }
}