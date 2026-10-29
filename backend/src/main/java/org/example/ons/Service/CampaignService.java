
        package org.example.ons.Service;

import lombok.AllArgsConstructor;
import org.example.ons.Api.ApiException;
import org.example.ons.Model.Campaign;
import org.example.ons.Repository.CampaignRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;

    // Create Campaign
    public void createCampaign(Campaign campaign) {
        campaign.setStatus("PLANNED");
        campaignRepository.save(campaign);

    }

    // Get All Campaigns
    public List<Campaign> getAllCampaigns() {
        return campaignRepository.findAll();
    }

    // Get Campaign By ID
    public Campaign getCampaignById(Integer id) {
        Campaign campaign = campaignRepository.findCampaignById(id);

        if (campaign == null) {
            throw new ApiException("Campaign not found with ID: " + id);
        }

        return campaign;
    }

    // Update Campaign
    public void updateCampaign(Integer id, Campaign updatedCampaign) {

        Campaign existingCampaign = campaignRepository.findCampaignById(id);

 if(existingCampaign==null){
    throw new ApiException("Campaign not found with ID: " + id);
 }
        existingCampaign.setCampaignName(updatedCampaign.getCampaignName());
        existingCampaign.setContactNumber(updatedCampaign.getContactNumber());
        existingCampaign.setStartDate(updatedCampaign.getStartDate());
        existingCampaign.setEndDate(updatedCampaign.getEndDate());
        existingCampaign.setStatus(updatedCampaign.getStatus());

        campaignRepository.save(existingCampaign);
    }

    // Delete Campaign
    public void deleteCampaign(Integer id) {
        Campaign campaign = getCampaignById(id);
        campaignRepository.delete(campaign);
    }


}
