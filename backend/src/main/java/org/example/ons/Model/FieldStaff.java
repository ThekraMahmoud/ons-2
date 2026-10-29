package org.example.ons.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FieldStaff {
    @Id
    private Integer id;

    @OneToMany(mappedBy = "fieldStaff")
    private Set<AssistanceReport> assistanceReports;

    @OneToOne
    @MapsId
    @PrimaryKeyJoinColumn
    private User user;
}
