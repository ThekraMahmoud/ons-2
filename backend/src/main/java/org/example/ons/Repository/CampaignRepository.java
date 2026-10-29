package org.example.ons.Repository;

import org.example.ons.Model.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Integer> {

    Campaign findCampaignById(Integer campaignId);

    @Query(value = """
        SELECT *
        FROM campaign
        WHERE user_id = :campaignId
        FOR UPDATE
        """, nativeQuery = true)
    Optional<Campaign> findCampaignForPayment(
            @Param("campaignId") Integer campaignId
    );
}
