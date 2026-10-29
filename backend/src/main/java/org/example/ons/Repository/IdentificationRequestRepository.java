package org.example.ons.Repository;

import org.example.ons.Model.IdentificationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdentificationRequestRepository
        extends JpaRepository<IdentificationRequest, Long> {
}
