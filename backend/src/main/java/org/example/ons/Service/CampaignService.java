package org.example.ons.Service;

import lombok.AllArgsConstructor;
import org.example.ons.Api.ApiException;
import org.example.ons.DTO.AuthorizedStaffDTO;
import org.example.ons.DTO.CampaignAnalyticsDTO;
import org.example.ons.Enum.ReportStatus;
import org.example.ons.Model.*;
import org.example.ons.Repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@AllArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final NotificationService notificationService;
    private final PilgrimRepository pilgrimRepository;
    private final AssistanceReportRepository assistanceReportRepository;
    private final EmailService emailService;

    // Dependencies added from the other branch
    private final AccommodationRepository accommodationRepository;
    private final AuthorizedStaffRepository authorizedStaffRepository;
    private final AuthorizedStaffService authorizedStaffService;
    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    // Create Campaign
    public Campaign createCampaign(Campaign campaign) {
        campaign.setId(null);
        campaign.setStatus("PLANNED");

        User owner = campaign.getUser();
        if (owner == null) {
            throw new ApiException("Campaign owner is required");
        }

        if (authRepository.findUserByEmail(owner.getEmail()) != null) {
            throw new ApiException("Email is already registered");
        }

        owner.setId(null);
        owner.setRole("CAMPAIGN_OWNER"); // يحدده الخادم، لا الواجهة
        owner.setActive(false);          // يبقى غير نشط حتى تأكيد الدفع
        owner.setPassword(passwordEncoder.encode(owner.getPassword()));


        User savedOwner = authRepository.save(owner);
        campaign.setUser(savedOwner);

        Campaign savedCampaign = campaignRepository.save(campaign);

        notificationService.notifyCampaignAdded(
                savedCampaign,
                savedOwner.getEmail()
        );


        return savedCampaign;
    }

    // Get All Campaigns
    public List<Campaign> getAllCampaigns() {
        return campaignRepository.findAll();
    }

    // Get Campaign By ID
    public Campaign getCampaignById(Integer id) {
        return getRequiredCampaign(id);
    }

    // Update Campaign
    @Transactional
    public void updateCampaign(
            Integer id,
            Campaign updatedCampaign) {

        Campaign existingCampaign = getRequiredCampaign(id);

        if (updatedCampaign.getUser() == null) {
            throw new ApiException(
                    "Campaign owner details are required"
            );
        }

        User owner = existingCampaign.getUser();

        owner.setName(updatedCampaign.getUser().getName());
        owner.setPhoneNumber(
                updatedCampaign.getUser().getPhoneNumber()
        );
        owner.setEmail(updatedCampaign.getUser().getEmail());

        existingCampaign.setCampaignName(
                updatedCampaign.getCampaignName()
        );
        existingCampaign.setStartDate(updatedCampaign.getStartDate());
        existingCampaign.setEndDate(updatedCampaign.getEndDate());
        existingCampaign.setStatus(updatedCampaign.getStatus());

        authRepository.save(owner);
        campaignRepository.save(existingCampaign);
    }

    // Delete Campaign
    public void deleteCampaign(Integer id) {
        Campaign campaign = getRequiredCampaign(id);
        campaignRepository.delete(campaign);
    }

    // Update campaign status
    public void updateCampaignStatus(Integer campaignId, String status) {
        Campaign campaign = getRequiredCampaign(campaignId);

        if (status == null ||
                !status.matches("PLANNED|ACTIVE|COMPLETED|CANCELLED")) {
            throw new ApiException(
                    "Invalid campaign status. Allowed values: PLANNED, ACTIVE, COMPLETED, CANCELLED"
            );
        }

        campaign.setStatus(status);
        campaignRepository.save(campaign);
    }

    // Get pilgrims belonging to a campaign
    @Transactional(readOnly = true)
    public List<Pilgrim> getCampaignPilgrims(Integer campaignId) {
        getRequiredCampaign(campaignId);
        return pilgrimRepository.findByCampaign_Id(campaignId);
    }

    // Get reports belonging to a campaign
    @Transactional(readOnly = true)
    public List<AssistanceReport> getCampaignReports(Integer campaignId) {
        getRequiredCampaign(campaignId);
        return assistanceReportRepository.findByCampaign_Id(campaignId);
    }

    // Get authorized staff belonging to a campaign
    @Transactional(readOnly = true)
    public List<AuthorizedStaff> getCampaignStaff(Integer campaignId) {
        getRequiredCampaign(campaignId);
        return authorizedStaffRepository.findByCampaign_Id(campaignId);
    }

    public void addStaffToCampaign(
            User actor,
            Integer campaignId,
            AuthorizedStaffDTO staffDTO) {

        authorizedStaffService.createStaff(
                actor,
                staffDTO,
                campaignId
        );
    }

    // Remove authorized staff from a campaign
    public void removeStaffFromCampaign(
            User actor,
            Integer campaignId,
            Integer userId) {

        getRequiredCampaign(campaignId);

        AuthorizedStaff staff =
                authorizedStaffRepository.findAuthorizedStaffById(userId);

        if (staff == null) {
            throw new ApiException(
                    "Authorized staff not found with ID: " + userId
            );
        }

        if (staff.getCampaign() == null
                || !campaignId.equals(staff.getCampaign().getId())) {
            throw new ApiException(
                    "The authorized staff member does not belong to this campaign"
            );
        }

        authorizedStaffService.deleteStaff(actor, userId);
    }

    // Get campaign statistics
    @Transactional(readOnly = true)
    public Map<String, Object> getCampaignStatistics(Integer campaignId) {
        getRequiredCampaign(campaignId);

        List<Pilgrim> pilgrims = pilgrimRepository.findByCampaign_Id(campaignId);
        List<AssistanceReport> reports =
                assistanceReportRepository.findByCampaign_Id(campaignId);
        List<AuthorizedStaff> staff =
                authorizedStaffRepository.findByCampaign_Id(campaignId);
        List<Accommodation> accommodations =
                accommodationRepository.findByCampaign_Id(campaignId);

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("campaignId", campaignId);
        statistics.put("totalPilgrims", pilgrims.size());
        statistics.put("totalReports", reports.size());
        statistics.put("totalStaff", staff.size());
        statistics.put("totalAccommodations", accommodations.size());

        statistics.put(
                "openReports",
                assistanceReportRepository.countByStatusAndCampaign_Id(
                        ReportStatus.OPEN, campaignId
                )
        );
        statistics.put(
                "assignedReports",
                assistanceReportRepository.countByStatusAndCampaign_Id(
                        ReportStatus.ASSIGNED, campaignId
                )
        );
        statistics.put(
                "inProgressReports",
                assistanceReportRepository.countByStatusAndCampaign_Id(
                        ReportStatus.IN_PROGRESS, campaignId
                )
        );
        statistics.put(
                "resolvedReports",
                assistanceReportRepository.countByStatusAndCampaign_Id(
                        ReportStatus.RESOLVED, campaignId
                )
        );

        return statistics;
    }

    // Get accommodations belonging to a campaign
    @Transactional(readOnly = true)
    public List<Accommodation> getCampaignAccommodations(Integer campaignId) {
        getRequiredCampaign(campaignId);
        return accommodationRepository.findByCampaign_Id(campaignId);
    }

    // Detailed campaign analytics report (kept from your version)
    @Transactional(readOnly = true)
    public CampaignAnalyticsDTO getCampaignAnalytics(Integer campaignId) {
        Campaign campaign = getRequiredCampaign(campaignId);

        List<Pilgrim> pilgrims = pilgrimRepository.findByCampaign_Id(campaignId);
        List<AssistanceReport> reports =
                assistanceReportRepository.findByCampaign_Id(campaignId);

        long totalPilgrims = pilgrims.size();
        long totalReports = reports.size();

        long pilgrimsWithReports = reports.stream()
                .filter(r -> r.getPilgrim() != null)
                .map(r -> r.getPilgrim().getId())
                .filter(Objects::nonNull)
                .distinct()
                .count();

        double reportRate = totalPilgrims == 0
                ? 0
                : (double) pilgrimsWithReports / totalPilgrims * 100;

        long openReports = reports.stream()
                .filter(r -> r.getStatus() == ReportStatus.OPEN)
                .count();
        long assignedReports = reports.stream()
                .filter(r -> r.getStatus() == ReportStatus.ASSIGNED)
                .count();
        long inProgressReports = reports.stream()
                .filter(r -> r.getStatus() == ReportStatus.IN_PROGRESS)
                .count();
        long resolvedReports = reports.stream()
                .filter(r -> r.getStatus() == ReportStatus.RESOLVED)
                .count();
        long cancelledReports = reports.stream()
                .filter(r -> r.getStatus() == ReportStatus.CANCELLED)
                .count();

        double resolutionRate = totalReports == 0
                ? 0
                : (double) resolvedReports / totalReports * 100;

        List<String> recommendations = new ArrayList<>();

        if (openReports > 0) {
            recommendations.add("There are " + openReports
                    + " open reports that require follow-up.");
        }
        if (assignedReports > 0) {
            recommendations.add("There are " + assignedReports
                    + " assigned reports awaiting follow-up.");
        }
        if (inProgressReports > 0) {
            recommendations.add("There are " + inProgressReports
                    + " reports currently in progress.");
        }
        if (cancelledReports > 0) {
            recommendations.add("There are " + cancelledReports
                    + " cancelled reports.");
        }
        if (totalReports > 0 && resolutionRate < 70) {
            recommendations.add("The report resolution rate is low. "
                    + "Improve report follow-up and resource allocation.");
        }
        if (totalPilgrims > 0 && reportRate >= 20) {
            recommendations.add("The percentage of pilgrims with reports is high. "
                    + "Review the campaign's needs and available resources.");
        }
        if (recommendations.isEmpty()) {
            recommendations.add(
                    "No additional recommendations based on the current statistics."
            );
        }

        return new CampaignAnalyticsDTO(
                campaign.getId(),
                campaign.getCampaignName(),
                totalPilgrims,
                pilgrimsWithReports,
                Math.round(reportRate * 100.0) / 100.0,
                totalReports,
                openReports,
                assignedReports,
                inProgressReports,
                resolvedReports,
                cancelledReports,
                Math.round(resolutionRate * 100.0) / 100.0,
                recommendations
        );
    }

    // Send the campaign analytics report by email (kept from your version)
    public void sendCampaignAnalyticsEmail(Integer campaignId) throws Exception {
        Campaign campaign = getRequiredCampaign(campaignId);

        String email = campaign.getUser().getEmail();
        if (email == null || email.isBlank()) {
            throw new ApiException("Campaign email not found");
        }

        CampaignAnalyticsDTO dto = getCampaignAnalytics(campaignId);
        String subject = "تقرير حملة أُنس - " + dto.getCampaignName();

        String recommendations = dto.getRecommendations() == null
                || dto.getRecommendations().isEmpty()
                ? "لا توجد توصيات إضافية."
                : String.join("\n", dto.getRecommendations());

        String html = """
                <!DOCTYPE html>
                <html lang="ar" dir="rtl">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>تقرير الحملة</title>
                </head>
                <body style="margin:0;padding:24px;background:#f5f7fa;
                             font-family:Arial,sans-serif;color:#263238;direction:rtl;">
                    <div style="max-width:700px;margin:auto;background:#ffffff;
                                padding:30px;border-radius:12px;">
                        <h2 style="color:#087e75;text-align:center;">
                            تقرير إحصائيات الحملة
                        </h2>
                        <h3 style="color:#087e75;">معلومات الحملة</h3>
                        <p><strong>اسم الحملة:</strong> %s</p>
                        <p><strong>رقم الحملة:</strong> %d</p>
                        <hr style="border:0;border-top:1px solid #e0e0e0;">
                        <h3 style="color:#087e75;">إحصائيات الحجاج</h3>
                        <p>إجمالي الحجاج: %d</p>
                        <p>الحجاج الذين لديهم بلاغات: %d</p>
                        <p>نسبة الحجاج الذين لديهم بلاغات: %.2f%%</p>
                        <hr style="border:0;border-top:1px solid #e0e0e0;">
                        <h3 style="color:#087e75;">إحصائيات البلاغات</h3>
                        <p>إجمالي البلاغات: %d</p>
                        <p>البلاغات المفتوحة: %d</p>
                        <p>البلاغات المسندة: %d</p>
                        <p>البلاغات قيد المعالجة: %d</p>
                        <p>البلاغات المحلولة: %d</p>
                        <p>البلاغات الملغاة: %d</p>
                        <div style="background:#e8f5f3;padding:15px;border-radius:8px;">
                            <strong>نسبة حل البلاغات: %.2f%%</strong>
                        </div>
                        <h3 style="color:#087e75;">التوصيات</h3>
                        <div style="white-space:pre-line;line-height:1.8;">%s</div>
                        <hr style="border:0;border-top:1px solid #e0e0e0;">
                        <p style="text-align:center;color:#78909c;font-size:12px;">
                            أُنس | في ضيافة الرحمن
                        </p>
                    </div>
                </body>
                </html>
                """.formatted(
                escapeHtml(dto.getCampaignName()),
                dto.getCampaignId(),
                dto.getTotalPilgrims(),
                dto.getPilgrimsWithReports(),
                dto.getReportRate(),
                dto.getTotalReports(),
                dto.getOpenReports(),
                dto.getAssignedReports(),
                dto.getInProgressReports(),
                dto.getResolvedReports(),
                dto.getCancelledReports(),
                dto.getResolutionRate(),
                escapeHtml(recommendations)
        );

        emailService.sendHtmlEmail(email, subject, html);
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    // Validate campaign existence
    private Campaign getRequiredCampaign(Integer campaignId) {
        Campaign campaign = campaignRepository.findCampaignById(campaignId);

        if (campaign == null) {
            throw new ApiException("Campaign not found with ID: " + campaignId);
        }

        return campaign;
    }
}
