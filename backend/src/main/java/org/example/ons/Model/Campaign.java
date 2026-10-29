        package org.example.ons.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@Check(constraints = "end_date >= start_date")
public class Campaign {

      @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @NotBlank(message = "Campaign name is required")
    @Size(max = 100, message = "Campaign name must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String campaignName;
//
//    @Pattern(
//            regexp = "^\\+?[0-9]{8,15}$",
//            message = "Invalid contact number"
//    )
//    @Column(length = 16)
//    private String contactNumber;
//
//    @NotBlank(message = "Campaign email is required")
//    @Email(message = "Campaign email must be valid")
//    @Size(max = 254, message = "Campaign email must not exceed 254 characters")
//    @Column(length = 254,nullable = false,unique = true)
//    private String email;
//
//
//    @JsonIgnore
//    private String password;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Pattern(
            regexp = "PLANNED|ACTIVE|COMPLETED|CANCELLED",
            message = "Invalid campaign status"
    )
    @Column(nullable = false, length = 20)
    private String status;

    @OneToMany(mappedBy = "campaign")
    @JsonIgnore
    private Set<Pilgrim> pilgrims;

    @OneToMany(mappedBy = "campaign")
    @JsonIgnore
    private Set<Accommodation> accommodations;

    @OneToOne
    @MapsId
    @PrimaryKeyJoinColumn
    private User user;
}