        package org.example.ons.Service;

import lombok.AllArgsConstructor;
import org.example.ons.Api.ApiException;
import org.example.ons.Model.Campaign;
import org.example.ons.Model.Pilgrim;
import org.example.ons.Repository.CampaignRepository;
import org.example.ons.Repository.PilgrimRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PilgrimService {

    private final PilgrimRepository pilgrimRepository;
    private final CampaignRepository  campaignRepository;

    // Create Pilgrim
    public void createPilgrim(Integer campaignId, Pilgrim pilgrim) {

        Campaign campaign = campaignRepository.findCampaignById(campaignId);

        if (campaign == null) {
            throw new ApiException("Campaign not found with ID: " + campaignId);
        }

        pilgrim.setCampaign(campaign);
        pilgrimRepository.save(pilgrim);
    }

    // Get All Pilgrims
    public List<Pilgrim> getAllPilgrims() {
        return pilgrimRepository.findAll();
    }

    // Get Pilgrim By ID
    public Pilgrim getPilgrimById(Integer id) {
        Pilgrim pilgrim = pilgrimRepository.findPilgrimById(id);

        if (pilgrim == null) {
            throw new ApiException("Pilgrim not found with ID: " + id);
        }
        return pilgrim;
    }

    // Update Pilgrim
    public void updatePilgrim(Integer id, Pilgrim updatedPilgrim) {

        Pilgrim existingPilgrim = pilgrimRepository.findPilgrimById(id);

        if (existingPilgrim == null) {
            throw new ApiException("Pilgrim not found with ID: " + id);
        }

        existingPilgrim.setFullName(updatedPilgrim.getFullName());
        existingPilgrim.setNationality(updatedPilgrim.getNationality());
        existingPilgrim.setPhoneNumber(updatedPilgrim.getPhoneNumber());
        existingPilgrim.setVisaNumber(updatedPilgrim.getVisaNumber());
        existingPilgrim.setCampaign(updatedPilgrim.getCampaign());
        existingPilgrim.setAccommodation(updatedPilgrim.getAccommodation());

        pilgrimRepository.save(existingPilgrim);
    }

    // Delete Pilgrim
    public void deletePilgrim(Integer id) {
        Pilgrim pilgrim = pilgrimRepository.findPilgrimById(id);

        if (pilgrim == null) {
            throw new ApiException("Pilgrim not found with ID: " + id);
        }
        pilgrimRepository.delete(pilgrim);
    }
}
