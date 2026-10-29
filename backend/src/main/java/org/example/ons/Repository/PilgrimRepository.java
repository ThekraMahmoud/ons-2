package org.example.ons.Repository;

import org.example.ons.Model.Pilgrim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PilgrimRepository extends JpaRepository<Pilgrim ,Integer> {
    Pilgrim findPilgrimById (Integer id);
}
