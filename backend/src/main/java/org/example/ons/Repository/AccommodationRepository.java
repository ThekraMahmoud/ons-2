package org.example.ons.Repository;

import org.example.ons.Model.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccommodationRepository extends JpaRepository<Accommodation , Integer> {
    Accommodation findAccommodationById(Integer id);
}
