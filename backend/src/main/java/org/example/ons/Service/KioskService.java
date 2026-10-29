
package org.example.ons.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.example.ons.Api.ApiException;
import org.example.ons.DTO.KioskActivityDTO;
import org.example.ons.DTO.KioskReportDTO;
import org.example.ons.DTO.KioskUsageDTO;
import org.example.ons.Model.AssistanceReport;
import org.example.ons.Model.Kiosk;
import org.example.ons.Model.User;
import org.example.ons.Repository.AssistanceReportRepository;
import org.example.ons.Repository.KioskRepository;
import org.example.ons.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class KioskService {

    private final KioskRepository kioskRepository;
    private final AssistanceReportRepository assistanceReportRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    // Create Kiosk
    public void createKiosk(Kiosk kiosk) {
        kioskRepository.save(kiosk);
    }

    // Get All Kiosks
    public List<Kiosk> getAllKiosks() {
        return kioskRepository.findAll();
    }

    // Get Kiosk By ID
    public Kiosk getKioskById(Integer id) {
        Kiosk kiosk = kioskRepository.findKioskById(id);

        if (kiosk == null) {
            throw new ApiException("Kiosk not found with ID: " + id);
        }

        return kiosk;
    }

    // Update Kiosk
    public void updateKiosk(Integer id, Kiosk updatedKiosk) {

        Kiosk existingKiosk = getKioskById(id);

        existingKiosk.setKioskCode(updatedKiosk.getKioskCode());
        existingKiosk.setLocationName(updatedKiosk.getLocationName());
        existingKiosk.setLatitude(updatedKiosk.getLatitude());
        existingKiosk.setLongitude(updatedKiosk.getLongitude());
        existingKiosk.setStatus(updatedKiosk.getStatus());

        kioskRepository.save(existingKiosk);
    }

    // Delete Kiosk
     public void deleteKiosk(Integer id) {
        Kiosk kiosk = getKioskById(id);
        kioskRepository.delete(kiosk);
    }




    // Update Kiosk Status
    public void updateKioskStatus(Integer id, String status) {

        Kiosk kiosk = getKioskById(id);

        if (status == null || !status.matches("ACTIVE|INACTIVE|MAINTENANCE")) {
            throw new ApiException("Status must be ACTIVE, INACTIVE, or MAINTENANCE");
        }
        kiosk.setStatus(status);
        kioskRepository.save(kiosk);
    }



    public void updateLocation(Integer kioskId, Kiosk kiosk) {

        Kiosk kioskLocation = kioskRepository.findKioskById(kioskId);

        if (kioskLocation == null) {
            throw new ApiException("Kiosk not found");
        }
        kioskLocation.setLatitude(kiosk.getLatitude());
        kioskLocation.setLongitude(kiosk.getLongitude());

        kioskRepository.save(kioskLocation);
    }


    //تسوي تحفظ اخر وقت لارسال الرساله
    public void updateAllKiosksHeartbeat() {

        List<Kiosk> kiosks = kioskRepository.findAll();

        for (Kiosk kiosk : kiosks) {
            kiosk.setLastHeartbeat(LocalDateTime.now());
        }

        kioskRepository.saveAll(kiosks);
    }


    public List<AssistanceReport> getAllReportForKiosk(Integer kioskId){
        Kiosk kiosk=kioskRepository.findKioskById(kioskId);

        if(kiosk==null){
            throw new ApiException("kiosk not found");
        }
        return assistanceReportRepository.findAssistanceReportByKiosk(kiosk);
    }



    //يجيب تقرير عن كل الاجهزه الموجوده في النظام من النشطه وغيرها
    //ياخذ اثنين dto  واحد يعرض بيانات الاجهزه النشطه وتفاصيلها والثاني ياخي الاحهزه الثانيه كم عددها
    public KioskActivityDTO getAllActivityDto() {

        List<Kiosk> kiosks = kioskRepository.findAll();

        long active = 0;
        long inactive = 0;
        long maintenance = 0;

        List<KioskUsageDTO> devicesActivity = new ArrayList<>();

        for (Kiosk kiosk : kiosks) {

            if ("ACTIVE".equals(kiosk.getStatus())) {
                active++;
            } else if ("INACTIVE".equals(kiosk.getStatus())) {
                inactive++;
            } else if ("MAINTENANCE".equals(kiosk.getStatus())) {
                maintenance++;
            }

            KioskUsageDTO dto = new KioskUsageDTO();
            dto.setKioskCode(kiosk.getKioskCode());
            dto.setLocationName(kiosk.getLocationName());
            dto.setStatus(kiosk.getStatus());
            dto.setTotalReports(assistanceReportRepository.countByKiosk(kiosk));
            devicesActivity.add(dto);
        }

        KioskActivityDTO result = new KioskActivityDTO();

        result.setTotalDevices(kiosks.size());
        result.setActiveDevices(active);
        result.setInactiveDevices(inactive);
        result.setMaintenanceDevices(maintenance);
        result.setDevicesActivity(devicesActivity);

        return result;
    }



    public KioskReportDTO getKioskReport(Integer adminId) {

        // 1. Verify administrator
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ApiException("User not found"));

        if (admin.getRole() == null ||
                !"ADMIN".equalsIgnoreCase(admin.getRole())) {
            throw new ApiException("Access denied: Admin role required");
        }

        // 2. Retrieve all kiosks
        List<Kiosk> kiosks = kioskRepository.findAll();

        KioskReportDTO report = new KioskReportDTO();
        report.setTotalKiosks(kiosks.size());

        List<KioskReportDTO.KioskUsageDTO> devices = new ArrayList<>();

        long active = 0;
        long inactive = 0;
        long maintenance = 0;

        long activeReports = 0;
        long inactiveReports = 0;
        long maintenanceReports = 0;
        long totalReports = 0;

        long makkahTotal = 0;
        long makkahActive = 0;
        long makkahInactive = 0;
        long makkahMaintenance = 0;
        long makkahReports = 0;

        long madinahTotal = 0;
        long madinahActive = 0;
        long madinahInactive = 0;
        long madinahMaintenance = 0;
        long madinahReports = 0;

        // 3. Process kiosks and calculate statistics
        for (Kiosk kiosk : kiosks) {

            String status = kiosk.getStatus() == null
                    ? ""
                    : kiosk.getStatus().toUpperCase();

            long kioskReports =
                    assistanceReportRepository.countByKiosk(kiosk);

            totalReports += kioskReports;

            // Count kiosks and reports by status
            switch (status) {
                case "ACTIVE" -> {
                    active++;
                    activeReports += kioskReports;
                }
                case "INACTIVE" -> {
                    inactive++;
                    inactiveReports += kioskReports;
                }
                case "MAINTENANCE" -> {
                    maintenance++;
                    maintenanceReports += kioskReports;
                }
            }

            // Classify kiosk by city
            String city = classifyKioskCity(kiosk);

            // Count kiosks and reports by city and status
            if ("Makkah".equals(city)) {

                makkahTotal++;
                makkahReports += kioskReports;

                switch (status) {
                    case "ACTIVE" -> makkahActive++;
                    case "INACTIVE" -> makkahInactive++;
                    case "MAINTENANCE" -> makkahMaintenance++;
                }

            } else if ("Madinah".equals(city)) {

                madinahTotal++;
                madinahReports += kioskReports;

                switch (status) {
                    case "ACTIVE" -> madinahActive++;
                    case "INACTIVE" -> madinahInactive++;
                    case "MAINTENANCE" -> madinahMaintenance++;
                }
            }

            // Build kiosk DTO
            KioskReportDTO.KioskUsageDTO device =
                    new KioskReportDTO.KioskUsageDTO();

            device.setKioskCode(kiosk.getKioskCode());
            device.setLocationName(kiosk.getLocationName());
            device.setStatus(kiosk.getStatus());
            device.setCity(city);
            device.setTotalReports(kioskReports);

            devices.add(device);
        }

        // 4. Sort kiosks by reports, highest first
        devices.sort(
                java.util.Comparator.comparingLong(
                        KioskReportDTO.KioskUsageDTO::getTotalReports
                ).reversed()
        );

        // 5. Set general statistics
        report.setActiveKiosks(active);
        report.setInactiveKiosks(inactive);
        report.setMaintenanceKiosks(maintenance);
        report.setTotalAssistanceReports(totalReports);

        report.setReportsFromActiveKiosks(activeReports);
        report.setReportsFromInactiveKiosks(inactiveReports);
        report.setReportsFromMaintenanceKiosks(maintenanceReports);

        // 6. Set Makkah statistics
        report.setMakkahTotalKiosks(makkahTotal);
        report.setMakkahActiveKiosks(makkahActive);
        report.setMakkahInactiveKiosks(makkahInactive);
        report.setMakkahMaintenanceKiosks(makkahMaintenance);
        report.setMakkahTotalReports(makkahReports);

        // 7. Set Madinah statistics
        report.setMadinahTotalKiosks(madinahTotal);
        report.setMadinahActiveKiosks(madinahActive);
        report.setMadinahInactiveKiosks(madinahInactive);
        report.setMadinahMaintenanceKiosks(madinahMaintenance);
        report.setMadinahTotalReports(madinahReports);

        // 8. Set all kiosk activity
        report.setDevicesActivity(devices);

        // 9. Select top five kiosks
        report.setMostRequestedKiosks(
                devices.stream()
                        .limit(5)
                        .toList()
        );

        // 10. Validate administrator email
        if (admin.getEmail() == null || admin.getEmail().isBlank()) {
            throw new ApiException("Administrator email is missing");
        }

        // 11. Build rows for the top-five table
        // The HTML table has four columns: Code, Location, Status, Reports.
        // City is intentionally excluded from this table.
        StringBuilder topKiosksHtml = new StringBuilder();

        for (KioskReportDTO.KioskUsageDTO kiosk
                : report.getMostRequestedKiosks()) {

            topKiosksHtml.append("<tr style=\"border-bottom:1px solid #e5ebe6;\">")
                    .append("<td style=\"padding:12px 8px;\">")
                    .append(escapeHtml(kiosk.getKioskCode()))
                    .append("</td>")

                    .append("<td style=\"padding:12px 8px;\">")
                    .append(escapeHtml(kiosk.getLocationName()))
                    .append("</td>")

                    .append("<td style=\"padding:12px 8px;\">")
                    .append(escapeHtml(kiosk.getStatus()))
                    .append("</td>")

                    .append("<td align=\"right\" style=\"padding:12px 8px;\">")
                    .append(kiosk.getTotalReports())
                    .append("</td>")

                    .append("</tr>");
        }

        // Show a message if there are no kiosks
        if (topKiosksHtml.isEmpty()) {
            topKiosksHtml.append("""
                <tr>
                    <td colspan="4"
                        style="padding:16px;text-align:center;color:#77877d;">
                        No kiosk report data available.
                    </td>
                </tr>
                """);
        }

        // 12. Load the external HTML template
        String html = loadKioskReportTemplate();

        // 13. Replace template placeholders with actual statistics
        html = html
                .replace("{{TOTAL}}",
                        String.valueOf(report.getTotalKiosks()))
                .replace("{{ACTIVE}}",
                        String.valueOf(report.getActiveKiosks()))
                .replace("{{INACTIVE}}",
                        String.valueOf(report.getInactiveKiosks()))
                .replace("{{MAINTENANCE}}",
                        String.valueOf(report.getMaintenanceKiosks()))
                .replace("{{REPORTS}}",
                        String.valueOf(report.getTotalAssistanceReports()))

                .replace("{{ACTIVE_REPORTS}}",
                        String.valueOf(report.getReportsFromActiveKiosks()))
                .replace("{{INACTIVE_REPORTS}}",
                        String.valueOf(report.getReportsFromInactiveKiosks()))
                .replace("{{MAINTENANCE_REPORTS}}",
                        String.valueOf(report.getReportsFromMaintenanceKiosks()))

                .replace("{{MAKKAH_TOTAL}}",
                        String.valueOf(report.getMakkahTotalKiosks()))
                .replace("{{MAKKAH_ACTIVE}}",
                        String.valueOf(report.getMakkahActiveKiosks()))
                .replace("{{MAKKAH_INACTIVE}}",
                        String.valueOf(report.getMakkahInactiveKiosks()))
                .replace("{{MAKKAH_MAINTENANCE}}",
                        String.valueOf(report.getMakkahMaintenanceKiosks()))
                .replace("{{MAKKAH_REPORTS}}",
                        String.valueOf(report.getMakkahTotalReports()))

                .replace("{{MADINAH_TOTAL}}",
                        String.valueOf(report.getMadinahTotalKiosks()))
                .replace("{{MADINAH_ACTIVE}}",
                        String.valueOf(report.getMadinahActiveKiosks()))
                .replace("{{MADINAH_INACTIVE}}",
                        String.valueOf(report.getMadinahInactiveKiosks()))
                .replace("{{MADINAH_MAINTENANCE}}",
                        String.valueOf(report.getMadinahMaintenanceKiosks()))
                .replace("{{MADINAH_REPORTS}}",
                        String.valueOf(report.getMadinahTotalReports()))

                .replace("{{TOP_KIOSKS}}", topKiosksHtml.toString());

        // 14. Send email using the existing EmailService
        try {
            emailService.sendHtmlEmail(
                    admin.getEmail(),
                    "ONS - Kiosk Usage Report",
                    html
            );
        } catch (Exception e) {
            throw new ApiException(
                    "Failed to send kiosk report email: " + e.getMessage()
            );
        }

        return report;
    }

    private String loadKioskReportTemplate() {

        try (var inputStream = new org.springframework.core.io.ClassPathResource(
                "templates/kiosk-report-template.html"
        ).getInputStream()) {

            return new String(
                    inputStream.readAllBytes(),
                    java.nio.charset.StandardCharsets.UTF_8
            );

        } catch (java.io.IOException e) {
            throw new ApiException(
                    "Failed to load kiosk report HTML template"
            );
        }
    }

    private String escapeHtml(String value) {

        if (value == null) {
            return "-";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String classifyKioskCity(Kiosk kiosk) {

        String location = kiosk.getLocationName();

        if (location == null) {
            return "Unknown";
        }

        String city = location.toLowerCase();

        if (city.contains("makkah")
                || city.contains("mecca")
                || city.contains("مكة")) {
            return "Makkah";
        }

        if (city.contains("madinah")
                || city.contains("medina")
                || city.contains("المدينة")) {
            return "Madinah";
        }

        return "Unknown";
    }



}




