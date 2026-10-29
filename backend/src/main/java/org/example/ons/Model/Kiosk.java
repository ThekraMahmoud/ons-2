
package org.example.ons.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
public class Kiosk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @NotBlank(message = "Kiosk code is required")
    @Size(max = 50, message = "Kiosk code must not exceed 50 characters")
    @Column(nullable = false, unique = true, length = 50)
    private String kioskCode;

    @NotBlank(message = "Location name is required")
    @Size(max = 100, message = "Location name must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String locationName;

    @DecimalMin(value = "-90.0", message = "Latitude must be at least -90")
    @DecimalMax(value = "90.0", message = "Latitude must not exceed 90")
    @Column(nullable = false)
    private Double latitude;

    @DecimalMin(value = "-180.0", message = "Longitude must be at least -180")
    @DecimalMax(value = "180.0", message = "Longitude must not exceed 180")
    @Column(nullable = false)
    private Double longitude;

    @NotBlank(message = "Status is required")
    @Pattern(
            regexp = "^(ACTIVE|INACTIVE|MAINTENANCE)$",
            message = "Status must be ACTIVE, INACTIVE, or MAINTENANCE"
    )
    @Column(nullable = false, length = 20)
    private String status;

    @OneToMany(mappedBy = "kiosk")
    @JsonIgnore
    private Set<AssistanceReport> assistanceReports;

    @Column
    private LocalDateTime lastHeartbeat;
}
