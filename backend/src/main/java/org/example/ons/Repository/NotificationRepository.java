package org.example.ons.Repository;

import org.example.ons.Model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByReport_ReportIdOrderBySentAtDesc(
            Long reportId
    );
}