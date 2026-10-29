package org.example.ons.Repository;

import jakarta.persistence.LockModeType;
import org.example.ons.Enum.ReportStatus;
import org.example.ons.Model.AssistanceReport;
import org.example.ons.Model.Campaign;
import org.example.ons.Model.Kiosk;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface AssistanceReportRepository
        extends JpaRepository<AssistanceReport, Long> {

    boolean existsByCaseNumber(String caseNumber);

    AssistanceReport findAssistanceReportByReportId(Long id);

    List<AssistanceReport> findAssistanceReportByKiosk(Kiosk kiosk);

    long countByKiosk(Kiosk kiosk);

    List<AssistanceReport> findAssistanceReportByAuthorizedStaff_IdAndStatus(
            Integer authorizedStaffId,
            ReportStatus status
    );

    List<AssistanceReport> findAssistanceReportByAuthorizedStaff_Id(
            Integer authorizedStaffId
    );

    long countByStatus(ReportStatus status);

    long countByStatusAndCampaign_Id(
            ReportStatus status,
            Integer campaignId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT report
            FROM AssistanceReport report
            WHERE report.reportId IN :reportIds
            ORDER BY report.reportId
            """)
    List<AssistanceReport> findReportsForUpdate(
            @Param("reportIds") Collection<Long> reportIds
    );

    List<AssistanceReport> findByCampaign_IdAndStatusInAndCreatedAtBefore(
            Integer campaignId,
            List<ReportStatus> statuses,
            LocalDateTime cutoff
    );

    List<AssistanceReport> findByStatusInAndCreatedAtBefore(
            List<ReportStatus> statuses,
            LocalDateTime cutoff
    );

    @EntityGraph(attributePaths = {"pilgrim", "kiosk"})
    List<AssistanceReport> findByCampaignAndStatusAndAuthorizedStaffIsNull(
            Campaign campaign,
            ReportStatus status
    );
}