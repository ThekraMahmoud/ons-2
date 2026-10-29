
package org.example.ons.Service;

import lombok.AllArgsConstructor;
import org.example.ons.Api.ApiException;
import org.example.ons.Model.Accommodation;
import org.example.ons.Repository.AccommodationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;

    // Create Accommodation
    public void createAccommodation(Accommodation accommodation) {
        accommodationRepository.save(accommodation);
    }

    // Get All Accommodations
    public List<Accommodation> getAllAccommodations() {
        return accommodationRepository.findAll();
    }

    // Get Accommodation By ID
    public Accommodation getAccommodationById(Integer id) {
        Accommodation accommodation = accommodationRepository.findAccommodationById(id);

        if (accommodation == null) {
            throw new ApiException("Accommodation not found with ID: " + id);
        }

        return accommodation;
    }

    // Update Accommodation
    public void updateAccommodation(Integer id, Accommodation updatedAccommodation) {

        Accommodation existingAccommodation = accommodationRepository.findAccommodationById(id);

        if (existingAccommodation == null) {
            throw new ApiException("Accommodation not found with ID: " + id);
        }

        existingAccommodation.setName(updatedAccommodation.getName());
        existingAccommodation.setAddress(updatedAccommodation.getAddress());
        existingAccommodation.setCity(updatedAccommodation.getCity());
        existingAccommodation.setCampaign(updatedAccommodation.getCampaign());

        accommodationRepository.save(existingAccommodation);
    }

    // Delete Accommodation
    public void deleteAccommodation(Integer id) {
        Accommodation accommodation = accommodationRepository.findAccommodationById(id);

        if (accommodation == null) {
            throw new ApiException("Accommodation not found with ID: " + id);
        }
        accommodationRepository.delete(accommodation);
    }
}
