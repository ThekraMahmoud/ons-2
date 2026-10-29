package org.example.ons.Controller;

import lombok.RequiredArgsConstructor;
import org.example.ons.Service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    // 1. GET /api/v1/dashboard/summary
    @GetMapping("/summary")
    public ResponseEntity<?> getSummary() {
        return ResponseEntity.ok(
                dashboardService.getSummary()
        );
    }

    // 2. GET /api/v1/dashboard/reports-by-status
    @GetMapping("/reports-by-status")
    public ResponseEntity<?> getReportsByStatus() {
        return ResponseEntity.ok(
                dashboardService.getReportsByStatus()
        );
    }

    // 3. GET /api/v1/dashboard/reports-trend
    @GetMapping("/reports-trend")
    public ResponseEntity<?> getReportsTrend() {
        return ResponseEntity.ok(
                dashboardService.getReportsTrend()
        );
    }

    // 4. GET /api/v1/dashboard/response-time
    @GetMapping("/response-time")
    public ResponseEntity<?> getResponseTime() {
        return ResponseEntity.ok(
                dashboardService.getResponseTime()
        );
    }

    // 5. GET /api/v1/dashboard/resolution-time
    @GetMapping("/resolution-time")
    public ResponseEntity<?> getResolutionTime() {
        return ResponseEntity.ok(
                dashboardService.getResolutionTime()
        );
    }
}