package org.example.ons.Repository;

import org.example.ons.Model.PilgrimCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PilgrimCardRepository extends JpaRepository<PilgrimCard, Integer> {

    PilgrimCard findPilgrimCardById(Integer id);

    PilgrimCard findPilgrimCardByCardNumber(String cardNumber);
}