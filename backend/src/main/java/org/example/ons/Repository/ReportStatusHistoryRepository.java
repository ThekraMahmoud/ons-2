package org.example.ons.Repository;

import org.example.ons.Model.ReportStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportStatusHistoryRepository
        extends JpaRepository<ReportStatusHistory, Integer> {

    ReportStatusHistory findTopByReport_ReportIdOrderByChangedAtDesc(
            Long reportId
    );

    List<ReportStatusHistory> findByReport_ReportIdOrderByChangedAtDesc(
            Long reportId
    );
}