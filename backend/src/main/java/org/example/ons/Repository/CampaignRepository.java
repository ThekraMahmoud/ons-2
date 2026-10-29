package org.example.ons.Repository;

import org.example.ons.Model.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Integer> {

    Campaign findCampaignById(Integer campaignId);


}