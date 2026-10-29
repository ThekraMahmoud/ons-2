        package org.example.ons.Controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.ons.Api.ApiResponse;
import org.example.ons.Model.Campaign;
import org.example.ons.Service.CampaignService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/campaigns")
@AllArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;

    // Create Campaign
    @PostMapping("/add")
    public ResponseEntity<?> createCampaign(Integer campaignId,@Valid @RequestBody Campaign campaign) {
        campaignService.createCampaign(campaign);

        return ResponseEntity.status(200).body(new ApiResponse("Campaign created successfully"));
    }

    // Get All Campaigns
    @GetMapping("/get")
    public ResponseEntity<?> getAllCampaigns() {
        return ResponseEntity.status(200).body(campaignService.getAllCampaigns());
    }

    // Get Campaign By ID
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getCampaignById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(campaignService.getCampaignById(id));
    }

    // Update Campaign
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCampaign(@PathVariable Integer id, @Valid @RequestBody Campaign campaign) {

        campaignService.updateCampaign(id, campaign);

        return ResponseEntity.status(200).body(new ApiResponse( "Campaign updated successfully"));
    }

    // Delete Campaign
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCampaign(@PathVariable Integer id) {

        campaignService.deleteCampaign(id);
        return ResponseEntity.status(200).body(new ApiResponse("Campaign deleted successfully"));
    }
}
