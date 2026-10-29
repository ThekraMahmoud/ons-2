package org.example.ons.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ons.Api.ApiResponse;
import org.example.ons.DTO.EmployeeUpdateDTO;
import org.example.ons.DTO.FiledStaffDTO;
import org.example.ons.Model.FieldStaff;
import org.example.ons.Service.FiledStaffService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/field-staff")
@RequiredArgsConstructor
public class FieldStaffController {
    private final FiledStaffService fieldStaffService;

    @PostMapping("/create")
    public ResponseEntity<?> createStaff(@Valid @RequestBody FiledStaffDTO dto) {

        fieldStaffService.RegisterFiledStaff(dto);

        return ResponseEntity.status(200).body(new ApiResponse("Field staff Registered successfully"));
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllStaff() {

        return ResponseEntity.status(200).body(fieldStaffService.getAllStaff());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getStaffById(@PathVariable Integer id) {

        return ResponseEntity.status(200).body(fieldStaffService.getStaffById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateStaff(
            @PathVariable Integer id,
            @Valid @RequestBody EmployeeUpdateDTO dto) {

        fieldStaffService.updateStaff(id, dto);

        return ResponseEntity.status(200)
                .body(new ApiResponse("Field staff updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteStaff(@PathVariable Integer id) {

        fieldStaffService.deleteStaff(id);

        return ResponseEntity.status(200).body(new ApiResponse("Field staff deleted successfully"));
    }

    @GetMapping("/availability")
    public ResponseEntity<?> getAvailableFieldStaff() {
        return ResponseEntity.status(200)
                .body(fieldStaffService.getAvailableFieldStaff());
    }

}
