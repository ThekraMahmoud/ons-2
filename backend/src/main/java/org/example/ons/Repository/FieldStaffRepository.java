package org.example.ons.Repository;

import org.example.ons.Enum.ReportStatus;
import org.example.ons.Model.FieldStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FieldStaffRepository extends JpaRepository<FieldStaff,Integer> {


    @Query("""
        SELECT staff
        FROM FieldStaff staff
        WHERE staff.user.active = true
          AND NOT EXISTS (
              SELECT report.reportId
              FROM AssistanceReport report
              WHERE report.fieldStaff = staff
                AND report.status IN :busyStatuses
          )
        """)
    List<FieldStaff> findAvailableStaff(
            @Param("busyStatuses") List<ReportStatus> busyStatuses
    );
}
