package org.example.ons.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.ons.Enum.AuthorizedStaffRole;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizedStaff {
    @Id
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthorizedStaffRole staffRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @OneToOne
    @MapsId
    @PrimaryKeyJoinColumn
    private User user;
}
