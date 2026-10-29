package org.example.ons.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.ons.Api.ApiResponse;
import org.example.ons.DTO.MergeReportsDTO;
import org.example.ons.Enum.ReportStatus;
import org.example.ons.Model.AssistanceReport;
import org.example.ons.Service.AssistanceReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class AssistanceReportController {
    private final AssistanceReportService assistanceReportService;


    @PostMapping("/add")
    public ResponseEntity<?> createReport(@RequestBody AssistanceReport report) {
        assistanceReportService.createReport(report);
        return ResponseEntity.status(200)
                .body(new ApiResponse("Report created successfully"));
    }

    @GetMapping("/get")
    public ResponseEntity<?> getAllReports() {
        List<AssistanceReport> reports = assistanceReportService.getAllReports();
        return ResponseEntity.status(200).body(reports);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getReportById(@PathVariable Long id) {
        AssistanceReport report = assistanceReportService.getReportById(id);
        return ResponseEntity.status(200).body(report);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateReport(
            @PathVariable Long id,
            @RequestBody AssistanceReport updatedReport) {

        assistanceReportService.updateReport(id, updatedReport);
        return ResponseEntity.status(200)
                .body(new ApiResponse("Report updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteReport(@PathVariable Long id) {
        assistanceReportService.deleteReport(id);
        return ResponseEntity.status(200)
                .body(new ApiResponse("Report deleted successfully"));
    }

    @GetMapping("/count/status/{status}")
    public ResponseEntity<?> countReportByStatus(
            @PathVariable ReportStatus status) {

        return ResponseEntity.status(200)
                .body(assistanceReportService.countReportByStatus(status));
    }

    @GetMapping("/count/campaign/{campaignId}/status/{status}")
    public ResponseEntity<?> countReportByStatusForCampaign(
            @PathVariable Integer campaignId,
            @PathVariable ReportStatus status) {

        return ResponseEntity.status(200)
                .body(assistanceReportService.countReportByStatusForCampaign(
                        status,
                        campaignId
                ));
    }

    @PostMapping("/{reportId}/merge")
    public ResponseEntity<?> mergeReports(
            @PathVariable Long reportId,
            @RequestBody @Valid MergeReportsDTO dto) {

        assistanceReportService.mergeReports(reportId, dto);

        return ResponseEntity.status(200)
                .body(new ApiResponse("Reports assigned successfully"));
    }

    @GetMapping("/campaign/{campaignId}/overdue")
    public ResponseEntity<?> getOverdueReportsForCampaign(
            @PathVariable Integer campaignId) {

        return ResponseEntity.status(200)
                .body(assistanceReportService
                        .getCampaignOverdueReports(campaignId));
    }
    @GetMapping("/overdue")
    public ResponseEntity<?> getAllOverdueReports() {

        return ResponseEntity.status(200)
                .body(assistanceReportService.getAllOverdueReports());
    }



}
