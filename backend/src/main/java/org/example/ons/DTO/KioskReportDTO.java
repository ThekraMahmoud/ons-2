
        package org.example.ons.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class KioskReportDTO {

    // General statistics
    private long totalKiosks;
    private long activeKiosks;
    private long inactiveKiosks;
    private long maintenanceKiosks;
    private long totalAssistanceReports;

    // Reports grouped by kiosk status
    private long reportsFromActiveKiosks;
    private long reportsFromInactiveKiosks;
    private long reportsFromMaintenanceKiosks;

    // Makkah statistics
    private long makkahTotalKiosks;
    private long makkahActiveKiosks;
    private long makkahInactiveKiosks;
    private long makkahMaintenanceKiosks;
    private long makkahTotalReports;

    // Madinah statistics
    private long madinahTotalKiosks;
    private long madinahActiveKiosks;
    private long madinahInactiveKiosks;
    private long madinahMaintenanceKiosks;
    private long madinahTotalReports;

    // Details of every kiosk
    private List<KioskUsageDTO> devicesActivity = new ArrayList<>();

    // Most requested kiosks, ordered by report count
    private List<KioskUsageDTO> mostRequestedKiosks = new ArrayList<>();

    @Getter
    @Setter
    public static class KioskUsageDTO {

        private String kioskCode;
        private String locationName;
        private String status;
        private String city;
        private long totalReports;
    }
}
