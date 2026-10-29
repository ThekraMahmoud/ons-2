package org.example.ons.Controller;

import lombok.RequiredArgsConstructor;
import org.example.ons.Api.ApiResponse;
import org.example.ons.Model.Notification;
import org.example.ons.Service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/add")
    public ResponseEntity<?> createNotification(
            @RequestBody Notification notification) {

        notificationService.createNotification(notification);
        return ResponseEntity.status(200)
                .body(new ApiResponse("Notification created successfully"));
    }

    @GetMapping("/get")
    public ResponseEntity<?> getAllNotifications() {
        List<Notification> notifications =
                notificationService.getAllNotifications();
        return ResponseEntity.status(200).body(notifications);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getNotificationById(@PathVariable Long id) {
        Notification notification =
                notificationService.getNotificationById(id);
        return ResponseEntity.status(200).body(notification);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateNotification(
            @PathVariable Long id,
            @RequestBody Notification updatedNotification) {

        notificationService.updateNotification(id, updatedNotification);
        return ResponseEntity.status(200)
                .body(new ApiResponse("Notification updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.status(200)
                .body(new ApiResponse("Notification deleted successfully"));
    }

}
