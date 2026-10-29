package org.example.ons.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.ons.Enum.IdentificationStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class IdentificationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @Column(nullable = false, length = 50)
    private String cardNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private IdentificationStatus status;

    @Column(length = 500)
    private String failureReason;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;

        if (status == null) {
            status = IdentificationStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
