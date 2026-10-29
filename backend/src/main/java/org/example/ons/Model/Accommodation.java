        package org.example.ons.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
public class Accommodation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @NotBlank(message = "Accommodation name is required")
    @Size(max = 100, message = "Accommodation name must not exceed 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Accommodation address is required")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    @Column(nullable = false, length = 255)
    private String address;

    @Pattern(
            regexp = "^(Makkah|Madinah)$",
            message = "City must be either Makkah or Madinah"
    )
    @Column(nullable = false, length = 20)
    private String city;

    @ManyToOne
    @JoinColumn
    private Campaign campaign;

    @OneToMany(mappedBy = "accommodation")
    @JsonIgnore
    private Set<Pilgrim> pilgrims;
}