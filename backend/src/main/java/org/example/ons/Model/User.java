package org.example.ons.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^05\\d{8}$",
            message = "Phone number must start with 05 and contain 10 digits")
    @Column(nullable = false)
    private String phoneNumber;

   @NotBlank(message = "Role is required")
    @Pattern(regexp = "ADMIN|AuthorizedStaff|FIELDـSTAFF",
            message = "Role must be ADMIN, AuthorizedStaff, or FIELDـSTAFF")
    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "user")
    @JsonIgnore
    private FieldStaff fieldStaff;

    @OneToOne(mappedBy = "user")
    @JsonIgnore
    private AuthorizedStaff authorizedStaff;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
