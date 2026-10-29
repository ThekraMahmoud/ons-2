package org.example.ons.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignAnalyticsDTO {

    private Integer campaignId;
    private String campaignName;

    // Pilgrim statistics
    private long totalPilgrims;
    private long pilgrimsWithReports;
    private double reportRate;

    // Report statistics
    private long totalReports;
    private long openReports;
    private long assignedReports;
    private long inProgressReports;
    private long resolvedReports;
    private long cancelledReports;

    // Resolution statistics
    private double resolutionRate;

    // Recommendations
    private List<String> recommendations;
}