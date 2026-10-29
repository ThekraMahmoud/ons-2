package org.example.ons.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.ons.Enum.ReportStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ReportStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    private AssistanceReport report;

    @Enumerated(EnumType.STRING)
    private ReportStatus oldStatus;

    @Enumerated(EnumType.STRING)
    private ReportStatus newStatus;

    private LocalDateTime changedAt;

    private Long durationMinutes;
}
