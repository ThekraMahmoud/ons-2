package org.example.ons.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.ons.Enum.ReportStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssistanceReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @Column(nullable = false, unique = true)
    private String caseNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status = ReportStatus.OPEN;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean reminderSubmitted = false;

    private LocalDateTime pickupTime;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pilgrim_id", nullable = false)
    private Pilgrim pilgrim;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "kiosk_id", nullable = false)
    private Kiosk kiosk;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_staff_id")
    private FieldStaff fieldStaff;

    //هنا انااضفت ذي العلاقه
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authorized_staff_id")
    private AuthorizedStaff authorizedStaff;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
