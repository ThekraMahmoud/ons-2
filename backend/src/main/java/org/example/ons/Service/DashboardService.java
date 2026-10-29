package org.example.ons.Service;

import lombok.RequiredArgsConstructor;
import org.example.ons.Enum.ReportStatus;
import org.example.ons.Model.AssistanceReport;
import org.example.ons.Model.ReportStatusHistory;
import org.example.ons.Repository.AssistanceReportRepository;
import org.example.ons.Repository.ReportStatusHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final AssistanceReportRepository assistanceReportRepository;
    private final ReportStatusHistoryRepository reportStatusHistoryRepository;

    // 1. ملخص عام للإحصائيات
    public Map<String, Object> getSummary() {

        List<AssistanceReport> reports =
                assistanceReportRepository.findAll();

        Map<String, Object> summary = new LinkedHashMap<>();

        long open = countByStatus(reports, ReportStatus.OPEN);
        long assigned = countByStatus(reports, ReportStatus.ASSIGNED);
        long inProgress = countByStatus(reports, ReportStatus.IN_PROGRESS);
        long resolved = countByStatus(reports, ReportStatus.RESOLVED);
        long cancelled = countByStatus(reports, ReportStatus.CANCELLED);

        summary.put("totalReports", reports.size());
        summary.put("openReports", open);
        summary.put("assignedReports", assigned);
        summary.put("inProgressReports", inProgress);
        summary.put("resolvedReports", resolved);
        summary.put("cancelledReports", cancelled);

        return summary;
    }

    // 2. توزيع البلاغات حسب الحالة
    public Map<String, Long> getReportsByStatus() {

        List<AssistanceReport> reports =
                assistanceReportRepository.findAll();

        Map<String, Long> result = new LinkedHashMap<>();

        for (ReportStatus status : ReportStatus.values()) {
            result.put(
                    status.name(),
                    countByStatus(reports, status)
            );
        }

        return result;
    }

    // 3. اتجاه إنشاء البلاغات خلال آخر 7 أيام
    public List<Map<String, Object>> getReportsTrend() {

        List<AssistanceReport> reports =
                assistanceReportRepository.findAll();

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6);

        Map<LocalDate, Long> dailyCounts = new TreeMap<>();

        for (int i = 0; i < 7; i++) {
            dailyCounts.put(startDate.plusDays(i), 0L);
        }

        for (AssistanceReport report : reports) {

            if (report.getCreatedAt() == null) {
                continue;
            }

            LocalDate createdDate = report.getCreatedAt().toLocalDate();

            if (!createdDate.isBefore(startDate)
                    && !createdDate.isAfter(today)) {

                dailyCounts.merge(createdDate, 1L, Long::sum);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();

        dailyCounts.forEach((date, count) -> {

            Map<String, Object> item = new LinkedHashMap<>();

            item.put("date", date);
            item.put("reportCount", count);

            result.add(item);
        });

        return result;
    }

    // 4. متوسط وقت الاستجابة بالدقائق
    // يُحسب من إنشاء البلاغ حتى أول انتقال إلى ASSIGNED.
    public Map<String, Object> getResponseTime() {

        List<AssistanceReport> reports =
                assistanceReportRepository.findAll();

        long totalMinutes = 0;
        long measuredReports = 0;

        for (AssistanceReport report : reports) {

            if (report.getCreatedAt() == null
                    || report.getReportId() == null) {
                continue;
            }

            List<ReportStatusHistory> history =
                    reportStatusHistoryRepository
                            .findByReport_ReportIdOrderByChangedAtDesc(
                                    report.getReportId()
                            );

            LocalDateTime firstAssignment = null;

            for (ReportStatusHistory item : history) {

                if (item.getNewStatus() == ReportStatus.ASSIGNED
                        && item.getChangedAt() != null) {

                    if (firstAssignment == null
                            || item.getChangedAt()
                            .isBefore(firstAssignment)) {

                        firstAssignment = item.getChangedAt();
                    }
                }
            }

            if (firstAssignment != null
                    && !firstAssignment.isBefore(report.getCreatedAt())) {

                totalMinutes += Duration.between(
                        report.getCreatedAt(),
                        firstAssignment
                ).toMinutes();

                measuredReports++;
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();

        result.put("unit", "minutes");
        result.put("measuredReports", measuredReports);

        result.put(
                "averageResponseTimeMinutes",
                measuredReports == 0
                        ? null
                        : (double) totalMinutes / measuredReports
        );

        return result;
    }

    // 5. متوسط وقت الحل بالدقائق
    // يُحسب من إنشاء البلاغ حتى تسجيل حالة RESOLVED.
    public Map<String, Object> getResolutionTime() {

        List<AssistanceReport> reports =
                assistanceReportRepository.findAll();

        long totalMinutes = 0;
        long measuredReports = 0;

        for (AssistanceReport report : reports) {

            if (report.getCreatedAt() == null
                    || report.getReportId() == null) {
                continue;
            }

            List<ReportStatusHistory> history =
                    reportStatusHistoryRepository
                            .findByReport_ReportIdOrderByChangedAtDesc(
                                    report.getReportId()
                            );

            LocalDateTime resolutionTime = null;

            for (ReportStatusHistory item : history) {

                if (item.getNewStatus() == ReportStatus.RESOLVED
                        && item.getChangedAt() != null) {

                    if (resolutionTime == null
                            || item.getChangedAt()
                            .isBefore(resolutionTime)) {

                        resolutionTime = item.getChangedAt();
                    }
                }
            }

            if (resolutionTime != null
                    && !resolutionTime.isBefore(report.getCreatedAt())) {

                totalMinutes += Duration.between(
                        report.getCreatedAt(),
                        resolutionTime
                ).toMinutes();

                measuredReports++;
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();

        result.put("unit", "minutes");
        result.put("measuredReports", measuredReports);

        result.put(
                "averageResolutionTimeMinutes",
                measuredReports == 0
                        ? null
                        : (double) totalMinutes / measuredReports
        );

        return result;
    }

    // دالة مساعدة لحساب البلاغات حسب الحالة
    private long countByStatus(
            List<AssistanceReport> reports,
            ReportStatus status) {

        return reports.stream()
                .filter(report -> report.getStatus() == status)
                .count();
    }
}