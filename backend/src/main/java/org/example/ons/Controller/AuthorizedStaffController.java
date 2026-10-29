package org.example.ons.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ons.Api.ApiResponse;
import org.example.ons.DTO.AuthorizedStaffDTO;
import org.example.ons.DTO.EmployeeUpdateDTO;
import org.example.ons.Enum.ReportStatus;
import org.example.ons.Model.AuthorizedStaff;
import org.example.ons.Model.User;
import org.example.ons.Service.AuthorizedStaffService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/authorized-staff")
@RequiredArgsConstructor
public class AuthorizedStaffController {
    private final AuthorizedStaffService authorizedStaffService;
    @PostMapping("/create/{campaignId}")
    public ResponseEntity<?> createStaff(
            @PathVariable Integer campaignId,
            @Valid @RequestBody AuthorizedStaffDTO dto) {

        authorizedStaffService.createStaff(dto, campaignId);

        return ResponseEntity.status(200).body(new ApiResponse("Authorized staff Registered successfully"));
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllStaff() {

        return ResponseEntity.status(200).body(authorizedStaffService.getAllStaff());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getStaffById(@PathVariable Integer id) {

        return ResponseEntity.status(200).body(authorizedStaffService.getStaffById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateStaff(
            @PathVariable Integer id,
            @Valid @RequestBody EmployeeUpdateDTO dto) {

        authorizedStaffService.updateStaff(id, dto);

        return ResponseEntity.status(200)
                .body(new ApiResponse("Authorized staff updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteStaff(@PathVariable Integer id) {

        authorizedStaffService.deleteStaff(id);

        return ResponseEntity.status(200).body(new ApiResponse("Authorized staff deleted successfully"));
    }

    @GetMapping("/get/employees/{supervisorId}")
    public ResponseEntity<?> getEmployees(
            @PathVariable Integer supervisorId) {

        return ResponseEntity.status(200)
                .body(authorizedStaffService.getEmployees(supervisorId));
    }

    @PostMapping("/add/employee/{supervisorId}")
    public ResponseEntity<?> addEmployee(
            @PathVariable Integer supervisorId,
            @RequestBody @Valid AuthorizedStaffDTO dto) {

        authorizedStaffService.addEmployee(supervisorId, dto);

        return ResponseEntity.status(200)
                .body(new ApiResponse("Employee added successfully"));
    }

    @PutMapping("/update/employee/{supervisorId}/{employeeId}")
    public ResponseEntity<?> updateEmployee(
            @PathVariable Integer supervisorId,
            @PathVariable Integer employeeId,
            @RequestBody @Valid EmployeeUpdateDTO dto) {

        authorizedStaffService.updateEmployee(
                supervisorId,
                employeeId,
                dto
        );

        return ResponseEntity.status(200)
                .body(new ApiResponse("Employee updated successfully"));
    }

    @DeleteMapping("/delete/employee/{supervisorId}/{employeeId}")
    public ResponseEntity<?> deleteEmployee(
            @PathVariable Integer supervisorId,
            @PathVariable Integer employeeId) {

        authorizedStaffService.deleteEmployee(supervisorId, employeeId);

        return ResponseEntity.status(200)
                .body(new ApiResponse("Employee deleted successfully"));
    }



    // قبول بلاغ حاج ضائع وإسناده للموظف المختص
    @PutMapping("/{staffId}/pilgrims/{pilgrimId}/reports/{reportId}/accept")
    public ResponseEntity<?> acceptPilgrim(@PathVariable Integer staffId, @PathVariable Integer pilgrimId, @PathVariable long reportId) {
        authorizedStaffService.acceptPilgrim(staffId, pilgrimId, reportId);
        return ResponseEntity.status(200).body(new ApiResponse("Pilgrim accepted successfully"));
    }

    // الانتقال إلى الحالة التالية للبلاغ
    @PutMapping("/{staffId}/reports/{reportId}/advance-status")
    public ResponseEntity<?> advanceReportStatus(@PathVariable Integer staffId, @PathVariable long reportId) {
        authorizedStaffService.advanceReportStatus(staffId, reportId);
        return ResponseEntity.status(200).body(new ApiResponse("Report status updated successfully"));

    }

    @GetMapping("/staff/{staffId}")
    public ResponseEntity<?> getStaffReports(
            @PathVariable Integer staffId) {

        return ResponseEntity.status(200)
                .body(authorizedStaffService.getStaffReports(staffId));
    }

    @GetMapping("/staff/{staffId}/status/{status}")
    public ResponseEntity<?> getStaffReportsByStatus(
            @PathVariable Integer staffId,
            @PathVariable ReportStatus status) {

        return ResponseEntity.status(200)
                .body(authorizedStaffService.getStaffReportsByStatus(
                        staffId,
                        status
                ));
    }

    @GetMapping("/{staffId}/nearby-reports")
    public ResponseEntity<?> getNearbyReports(
            @PathVariable Integer staffId,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {

        return ResponseEntity.status(200)
                .body(authorizedStaffService.getNearbyReports(
                        staffId,
                        latitude,
                        longitude
                ));
    }


}
