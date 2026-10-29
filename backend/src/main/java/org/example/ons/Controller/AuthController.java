package org.example.ons.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ons.Api.ApiResponse;
import org.example.ons.DTO.ChangePasswordDTO;
import org.example.ons.Service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @GetMapping("/get/all")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.status(200).body(authService.getAllUsers());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(authService.getUserById(id));
    }

    @PutMapping("/activate/{id}")
    public ResponseEntity<?> activateUser(@PathVariable Integer id) {
        authService.activateUser(id);
        return ResponseEntity.status(200)
                .body(new ApiResponse("User activated successfully"));
    }

    @PutMapping("/deactivate/{id}")
    public ResponseEntity<?> deactivateUser(@PathVariable Integer id) {
        authService.deactivateUser(id);
        return ResponseEntity.status(200)
                .body(new ApiResponse("User deactivated successfully"));
    }

    @PostMapping("/change-password/{id}")
    public ResponseEntity<?> changePassword(
            @PathVariable Integer id,
            @RequestBody @Valid ChangePasswordDTO changePasswordDTO) {

        authService.changePassword(id, changePasswordDTO);

        return ResponseEntity.status(200)
                .body(new ApiResponse("Password changed successfully"));
    }

}
