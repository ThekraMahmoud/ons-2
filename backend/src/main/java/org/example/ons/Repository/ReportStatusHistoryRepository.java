package org.example.ons.Repository;

import org.example.ons.Model.ReportStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportStatusHistoryRepository extends JpaRepository<ReportStatusHistory, Long> {

    ReportStatusHistory findTopByReport_ReportIdOrderByChangedAtDesc(Long reportId);
}