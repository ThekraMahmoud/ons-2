package org.example.ons.Service;

import lombok.AllArgsConstructor;
import org.example.ons.Api.ApiException;
import org.example.ons.Enum.AuthorizedStaffRole;
import org.example.ons.Model.*;
import org.example.ons.Repository.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;
    private final PilgrimRepository pilgrimRepository;
    private final AssistanceReportRepository assistanceReportRepository;
    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;
    private final AuthorizedStaffRepository authorizedStaffRepository;

    // Create Accommodation
    @Transactional
    public void createAccommodation(
            User user,
            Accommodation accommodation) {

        Integer campaignId = getSupervisorCampaignId(user);

        Campaign campaign = campaignRepository.findCampaignById(campaignId);

        if (campaign == null) {
            throw new ApiException("Campaign not found");
        }

        accommodation.setId(null);
        accommodation.setCampaign(campaign);

        if (accommodation.getStatus() == null) {
            accommodation.setStatus("ACTIVE");
        }

        accommodationRepository.save(accommodation);
    }


    // Get All Accommodations
    public List<Accommodation> getAllAccommodations(Integer userId) {

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new ApiException("User not found");
        }
        if (!"ADMIN".equals(user.getRole())) {
            throw new ApiException("Only admin can view all accommodations");
        }
        return accommodationRepository.findAll();
    }

    // Get Accommodation By ID
    @Transactional(readOnly = true)
    public Accommodation getAccommodationById(
            User user,
            Integer id) {

        if (user == null
                || user.getId() == null
                || !user.isActive()) {
            throw new AccessDeniedException(
                    "An active authenticated account is required"
            );
        }

        Accommodation accommodation = getRequiredAccommodation(id);

        if ("ADMIN".equals(user.getRole())) {
            return accommodation;
        }

        Campaign campaign = accommodation.getCampaign();

        if ("CAMPAIGN_OWNER".equals(user.getRole())) {

            if (campaign != null
                    && campaign.getUser() != null
                    && user.getId().equals(
                    campaign.getUser().getId())) {
                return accommodation;
            }
        }

        if ("AuthorizedStaff".equals(user.getRole())) {

            AuthorizedStaff staff = authorizedStaffRepository
                    .findAuthorizedStaffById(user.getId());

            if (staff != null
                    && staff.getCampaign() != null
                    && campaign != null
                    && staff.getCampaign().getId().equals(
                    campaign.getId())) {
                return accommodation;
            }
        }

        throw new AccessDeniedException(
                "This accommodation does not belong to your campaign"
        );
    }

    // Update Accommodation
    @Transactional
    public void updateAccommodation(
            User user,
            Integer id,
            Accommodation updatedAccommodation) {

        Integer campaignId = getSupervisorCampaignId(user);

        Accommodation existingAccommodation = getRequiredAccommodation(id);

        if (existingAccommodation.getCampaign() == null
                || !campaignId.equals(
                existingAccommodation.getCampaign().getId())) {
            throw new AccessDeniedException(
                    "This accommodation does not belong to your campaign"
            );
        }

        existingAccommodation.setName(updatedAccommodation.getName());
        existingAccommodation.setAddress(updatedAccommodation.getAddress());
        existingAccommodation.setCity(updatedAccommodation.getCity());
        existingAccommodation.setLatitude(updatedAccommodation.getLatitude());
        existingAccommodation.setLongitude(updatedAccommodation.getLongitude());

        accommodationRepository.save(existingAccommodation);
    }

    // Delete Accommodation - preserve existing functionality
    @Transactional
    public void deleteAccommodation(User user, Integer id) {

        Integer campaignId = getSupervisorCampaignId(user);

        Accommodation accommodation = getRequiredAccommodation(id);

        if (accommodation.getCampaign() == null
                || !campaignId.equals(accommodation.getCampaign().getId())) {
            throw new AccessDeniedException(
                    "This accommodation does not belong to your campaign"
            );
        }

        accommodationRepository.delete(accommodation);
    }
    // Update Accommodation Status
    @Transactional
    public void updateAccommodationStatus(
            User user,
            Integer id,
            String status) {

        Integer campaignId = getSupervisorCampaignId(user);

        Accommodation accommodation = getRequiredAccommodation(id);

        if (accommodation.getCampaign() == null
                || !campaignId.equals(accommodation.getCampaign().getId())) {
            throw new AccessDeniedException(
                    "This accommodation does not belong to your campaign"
            );
        }

        if (status == null || !status.matches("ACTIVE|INACTIVE")) {
            throw new ApiException(
                    "Invalid status. Allowed values: ACTIVE, INACTIVE"
            );
        }

        accommodation.setStatus(status);

        accommodationRepository.save(accommodation);
    }







    // Get Pilgrims in an Accommodation
    @Transactional(readOnly = true)
    public List<Pilgrim> getAccommodationPilgrims(Integer id) {
        getRequiredAccommodation(id);

        return pilgrimRepository.findByAccommodation_Id(id);
    }


    //حلوه للادمن
    // Get Reports for Pilgrims staying at an Accommodation
    @Transactional(readOnly = true)
    public List<AssistanceReport> getAccommodationReports(Integer id) {
        getRequiredAccommodation(id);

        List<Pilgrim> pilgrims =
                pilgrimRepository.findByAccommodation_Id(id);

        List<AssistanceReport> reports = new ArrayList<>();

        for (Pilgrim pilgrim : pilgrims) {
            reports.addAll(
                    assistanceReportRepository.findByPilgrim_Id(
                            pilgrim.getId()
                    )
            );
        }

        return reports;
    }

    //ترجع بيانات الحجاج للحمله
    @Transactional(readOnly = true)
    public List<Map<String, Object>>
    getCampaignAccommodationsWithPilgrimsAndReports(User user) {

        if (user == null
                || user.getId() == null
                || !user.isActive()) {
            throw new AccessDeniedException(
                    "An active authenticated account is required"
            );
        }

        Integer campaignId;

        if ("CAMPAIGN_OWNER".equals(user.getRole())) {

            campaignId = getSupervisorCampaignId(user);

        } else if ("AuthorizedStaff".equals(user.getRole())) {

            AuthorizedStaff staff = authorizedStaffRepository
                    .findAuthorizedStaffById(user.getId());

            if (staff == null || staff.getCampaign() == null) {
                throw new AccessDeniedException(
                        "You are not linked to a campaign"
                );
            }

            campaignId = staff.getCampaign().getId();

        } else {
            throw new AccessDeniedException(
                    "Only campaign owners and staff can access this endpoint"
            );
        }

        Campaign campaign =
                campaignRepository.findCampaignById(campaignId);

        if (campaign == null) {
            throw new ApiException("Campaign not found");
        }

        List<Accommodation> accommodations =
                accommodationRepository.findByCampaign_Id(campaignId);

        List<Map<String, Object>> result = new ArrayList<>();

        for (Accommodation accommodation : accommodations) {

            List<Pilgrim> pilgrims =
                    pilgrimRepository.findByAccommodation_Id(
                            accommodation.getId()
                    );

            List<Map<String, Object>> pilgrimsData =
                    new ArrayList<>();

            for (Pilgrim pilgrim : pilgrims) {

                List<AssistanceReport> reports =
                        assistanceReportRepository.findByPilgrim_Id(
                                pilgrim.getId()
                        );

                Map<String, Object> pilgrimData = new HashMap<>();
                pilgrimData.put("pilgrimId", pilgrim.getId());
                pilgrimData.put("reports", reports);

                pilgrimsData.add(pilgrimData);
            }

            Map<String, Object> accommodationData = new HashMap<>();
            accommodationData.put(
                    "accommodationId", accommodation.getId()
            );
            accommodationData.put(
                    "accommodationName", accommodation.getName()
            );
            accommodationData.put("address", accommodation.getAddress());
            accommodationData.put("city", accommodation.getCity());
            accommodationData.put("pilgrims", pilgrimsData);

            result.add(accommodationData);
        }

        return result;
    }

    // Get Accommodation Location
    @Transactional(readOnly = true)
    public Map<String, Object> getAccommodationLocation(Integer id) {
        Accommodation accommodation = getRequiredAccommodation(id);

        Map<String, Object> location = new HashMap<>();

        location.put("accommodationId", accommodation.getId());
        location.put("name", accommodation.getName());
        location.put("address", accommodation.getAddress());
        location.put("city", accommodation.getCity());
        location.put("latitude", accommodation.getLatitude());
        location.put("longitude", accommodation.getLongitude());

        return location;
    }

    // Validate Accommodation Existence
    private Accommodation getRequiredAccommodation(Integer id) {
        Accommodation accommodation =
                accommodationRepository.findAccommodationById(id);

        if (accommodation == null) {
            throw new ApiException(
                    "Accommodation not found with ID: " + id
            );
        }

        return accommodation;
    }


    private Integer getSupervisorCampaignId(User user) {

        if (user == null
                || user.getId() == null
                || !user.isActive()) {
            throw new AccessDeniedException(
                    "An active authenticated account is required"
            );
        }

        if ("CAMPAIGN_OWNER".equals(user.getRole())) {

            Campaign campaign = campaignRepository
                    .findCampaignById(user.getId());

            if (campaign == null
                    || campaign.getUser() == null
                    || !user.getId().equals(campaign.getUser().getId())) {
                throw new AccessDeniedException(
                        "You are not linked to a campaign"
                );
            }

            return campaign.getId();
        }

        if (!"AuthorizedStaff".equals(user.getRole())) {
            throw new AccessDeniedException(
                    "Only campaign owners and supervisors can manage accommodations"
            );
        }

        AuthorizedStaff staff = authorizedStaffRepository
                .findAuthorizedStaffById(user.getId());

        if (staff == null
                || staff.getStaffRole() != AuthorizedStaffRole.SUPERVISOR
                || staff.getCampaign() == null) {
            throw new AccessDeniedException(
                    "You must be a supervisor linked to a campaign"
            );
        }

        return staff.getCampaign().getId();
    }

}