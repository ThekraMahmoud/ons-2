package org.example.ons.Service;

import lombok.RequiredArgsConstructor;
import org.example.ons.Api.ApiException;
import org.example.ons.DTO.MergeReportsDTO;
import org.example.ons.Enum.AuthorizedStaffRole;
import org.example.ons.Enum.ReportStatus;
import org.example.ons.Model.*;
import org.example.ons.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
public class AssistanceReportService {
    private final AssistanceReportRepository assistanceReportRepository;
    private final PilgrimRepository pilgrimRepository;
    private final KioskRepository kioskRepository;
    private final CampaignRepository campaignRepository;
    private final AuthorizedStaffRepository authorizedStaffRepository;
    private final NotificationService notificationService;

    public void createReport(AssistanceReport report) {
        Pilgrim pilgrim = pilgrimRepository.findById(
                report.getPilgrim().getId()).orElse(null);

        if (pilgrim == null) {
            throw new ApiException("pilgrim not found");
        }

        Kiosk kiosk = kioskRepository.findById(
                report.getKiosk().getId()).orElse(null);

        if (kiosk == null) {
            throw new ApiException("kiosk not found");
        }

        Campaign campaign = campaignRepository.findCampaignById(
                report.getCampaign().getId());

        if (campaign == null) {
            throw new ApiException("campaign not found");
        }

        report.setPilgrim(pilgrim);
        report.setKiosk(kiosk);
        report.setCampaign(campaign);
        report.setStatus(ReportStatus.OPEN);
        assistanceReportRepository.save(report);
        List<AuthorizedStaff> supervisors =
                authorizedStaffRepository
                        .findAuthorizedStaffByCampaignAndStaffRole(
                                campaign,
                                AuthorizedStaffRole.SUPERVISOR
                        );

        for (AuthorizedStaff supervisor : supervisors) {
            if (supervisor.getUser().isActive()) {
                notificationService.notifyNewReport(
                        report,
                        supervisor.getUser().getEmail()
                );
            }
        }
    }

    public List<AssistanceReport> getAllReports() {
        List<AssistanceReport> assistanceReports= assistanceReportRepository.findAll();
        if(assistanceReports.isEmpty()){
            throw new ApiException("there is no reports in the system");
        }
        return assistanceReports;
    }

    public AssistanceReport getReportById(Long id) {
        AssistanceReport assistanceReport= assistanceReportRepository.findAssistanceReportByReportId(id);
        if(assistanceReport==null){
            throw new ApiException("report not found");
        }
               return assistanceReport;
    }

    public void updateReport(Long id, AssistanceReport updatedReport) {
        AssistanceReport report = getReportById(id);

        Pilgrim pilgrim = pilgrimRepository.findById(
                updatedReport.getPilgrim().getId()).orElse(null);

        if (pilgrim == null) {
            throw new ApiException("pilgrim not found");
        }

        Kiosk kiosk = kioskRepository.findById(
                updatedReport.getKiosk().getId()).orElse(null);

        if (kiosk == null) {
            throw new ApiException("kiosk not found");
        }

        Campaign campaign = campaignRepository.findCampaignById(
                updatedReport.getCampaign().getId());

        if (campaign == null) {
            throw new ApiException("campaign not found");
        }

        report.setPilgrim(pilgrim);
        report.setKiosk(kiosk);
        report.setCampaign(campaign);

        report.setPickupTime(updatedReport.getPickupTime());
         assistanceReportRepository.save(report);
    }

    public void deleteReport(Long id) {
        AssistanceReport report = getReportById(id);
        if(report==null){
            throw new ApiException("report not found");
        }
        assistanceReportRepository.delete(report);
    }


    public long countReportByStatus(ReportStatus status) {
        return assistanceReportRepository.countByStatus(status);
    }

    public long countReportByStatusForCampaign(
            ReportStatus status,
            Integer campaignId) {

        return assistanceReportRepository
                .countByStatusAndCampaign_Id(status, campaignId);
    }


    @Transactional
    public void mergeReports(Long reportId, MergeReportsDTO dto) {

        AuthorizedStaff staff = authorizedStaffRepository
                .findAuthorizedStaffById(dto.getStaffId());

        if (staff == null) {
            throw new ApiException("staff not found");
        }

        if (!staff.getUser().isActive()) {
            throw new ApiException("staff account is inactive");
        }

        if (staff.getCampaign() == null) {
            throw new ApiException("staff has no campaign");
        }

        Set<Long> reportIds = new TreeSet<>(dto.getReportIds());
        reportIds.add(reportId);

        if (reportIds.size() < 2) {
            throw new ApiException("Select at least two different reports");
        }

        List<AssistanceReport> reports =
                assistanceReportRepository.findReportsForUpdate(reportIds);

        if (reports.size() != reportIds.size()) {
            throw new ApiException("one or more reports were not found");
        }

        // Validate every report before changing any assignment.
        for (AssistanceReport report : reports) {

            if (!report.getCampaign().getId()
                    .equals(staff.getCampaign().getId())) {
                throw new ApiException(
                        "all reports must belong to the staff campaign"
                );
            }

            if (report.getAuthorizedStaff() != null) {
                throw new ApiException(
                        "report " + report.getReportId() + " is already assigned"
                );
            }

            if (report.getStatus() != ReportStatus.OPEN) {
                throw new ApiException(
                        "report " + report.getReportId() + " is not open"
                );
            }
        }

        for (AssistanceReport report : reports) {
            report.setAuthorizedStaff(staff);
            report.setStatus(ReportStatus.ASSIGNED);
        }

        assistanceReportRepository.saveAll(reports);
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {

                        for (AssistanceReport report : reports) {
                            notificationService.notifyReportAssigned(
                                    report,
                                    staff.getUser().getPhoneNumber()
                            );
                        }
                    }
                }
        );
    }

    public List<AssistanceReport> getCampaignOverdueReports(Integer campaign_id){
        Campaign campaign = campaignRepository.findCampaignById(campaign_id);

        if(campaign==null){
            throw new ApiException("campaign not found");
        }

        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(15);

        return assistanceReportRepository
                .findByCampaign_IdAndStatusInAndCreatedAtBefore(campaign_id,
                        List.of(
                                ReportStatus.OPEN,
                                ReportStatus.ASSIGNED,
                                ReportStatus.IN_PROGRESS
                        ),
                        cutoff
                );
    }


    public List<AssistanceReport> getAllOverdueReports() {

        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(15);

        return assistanceReportRepository
                .findByStatusInAndCreatedAtBefore(
                        List.of(
                                ReportStatus.OPEN,
                                ReportStatus.ASSIGNED,
                                ReportStatus.IN_PROGRESS
                        ),
                        cutoff
                );
    }


}
