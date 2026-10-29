package org.example.ons.Repository;

import org.example.ons.Model.Kiosk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KioskRepository extends JpaRepository<Kiosk ,Integer> {

    Kiosk findKioskById(Integer id);

    List<Kiosk> findByStatus(String status);

}
