package org.example.ons.Repository;

import org.example.ons.Model.Pilgrim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PilgrimRepository extends JpaRepository<Pilgrim, Integer> {

    Pilgrim findPilgrimById(Integer id);

    // Search by visa number
    Pilgrim findPilgrimByVisaNumber(String visaNumber);

    // Search by full name (case-insensitive)
    List<Pilgrim> findByFullNameContainingIgnoreCase(String fullName);

    List<Pilgrim> findByCampaign_Id(Integer campaignId);

    List<Pilgrim> findByAccommodation_Id(Integer accommodationId);
}
