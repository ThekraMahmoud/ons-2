
package org.example.ons.Controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.ons.Api.ApiResponse;
import org.example.ons.DTO.KioskActivityDTO;
import org.example.ons.Model.Kiosk;
import org.example.ons.Service.KioskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/kiosks")
@AllArgsConstructor
public class KioskController {

    private final KioskService kioskService;

    // Create Kiosk
    @PostMapping("/add")
    public ResponseEntity<?> addKiosk(@Valid @RequestBody Kiosk kiosk) {
        kioskService.createKiosk(kiosk);
        return ResponseEntity.status(200).body(new ApiResponse("Kiosk added successfully"));
    }

    // Get All Kiosks
    @GetMapping("/get")
    public ResponseEntity<?> getAllKiosks() {
        return ResponseEntity.status(200).body(kioskService.getAllKiosks());
    }

    // Get Kiosk By ID
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getKioskById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(kioskService.getKioskById(id));
    }

    // Update Kiosk
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateKiosk(@PathVariable Integer id, @Valid @RequestBody Kiosk kiosk) {

        kioskService.updateKiosk(id, kiosk);

        return ResponseEntity.status(200).body(new ApiResponse("Kiosk updated successfully"));
    }

    // Delete Kiosk
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteKiosk(@PathVariable Integer id) {
        kioskService.deleteKiosk(id);
        return ResponseEntity.status(200).body(new ApiResponse("Kiosk deleted successfully"));
    }


    // تحديث حالت الجهاز
   @PatchMapping("/update-status/{id}")
    public ResponseEntity<?> updateKioskStatus(@PathVariable Integer id, @RequestBody String status) {
        kioskService.updateKioskStatus(id,status);
        return ResponseEntity.ok(new ApiResponse("Kiosk status updated successfully")
        );
    }

    //تحديث موقع الجهاز
    @PatchMapping("/update-location/{kioskId}")
    public ResponseEntity<?> updateLocation(@PathVariable Integer kioskId, @RequestBody Kiosk kiosk) {

        kioskService.updateLocation(kioskId, kiosk);
        return ResponseEntity.ok(new ApiResponse("Kiosk location updated successfully")
        );
    }

    //اختبار الاشاره سويته داخل n8n ووضابط
    @PatchMapping("/heartbeat/all")
    public ResponseEntity<?> updateAllHeartbeats() {

        kioskService.updateAllKiosksHeartbeat();
        return ResponseEntity.ok(new ApiResponse("All Kiosks heartbeats updated successfully")
        );
    }

    // اجيب كل البلاغات الي صارت في الجهاز
    @GetMapping("/{kioskId}/reports")
    public ResponseEntity<?> getAllReportsForKiosk(@PathVariable Integer kioskId) {
        return ResponseEntity.ok(kioskService.getAllReportForKiosk(kioskId));
    }

    @GetMapping("/activity")
    public ResponseEntity<?> getAllActivity() {
        return ResponseEntity.ok(kioskService.getAllActivityDto());
    }

}
