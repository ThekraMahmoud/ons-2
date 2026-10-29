package org.example.ons.Repository;

import org.example.ons.Enum.AuthorizedStaffRole;
import org.example.ons.Model.AuthorizedStaff;
import org.example.ons.Model.Campaign;
import org.example.ons.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorizedStaffRepository extends JpaRepository<AuthorizedStaff,Integer> {
    AuthorizedStaff findAuthorizedStaffById(Integer staffId);

    List<AuthorizedStaff> findAuthorizedStaffByCampaignAndStaffRole(Campaign campaign, AuthorizedStaffRole authorizedStaffRole);

    List<AuthorizedStaff> findByCampaign_Id(Integer campaignId);
}
