package org.example.ons;

import org.example.ons.Api.ApiException;
import org.example.ons.DTO.PilgrimCardLookupDTO;
import org.example.ons.Enum.AuthorizedStaffRole;
import org.example.ons.Model.*;
import org.example.ons.Repository.*;
import org.example.ons.Service.PilgrimCardService;
import org.example.ons.Controller.PilgrimCardController;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PilgrimCardLookupTests {
    final PilgrimRepository pilgrims = mock(PilgrimRepository.class);
    final PilgrimCardRepository cards = mock(PilgrimCardRepository.class);
    final AuthorizedStaffRepository staff = mock(AuthorizedStaffRepository.class);
    final PilgrimCardService service = new PilgrimCardService(cards, pilgrims, staff);
    User actor(int id, String role) { User u = new User(); u.setId(id); u.setRole(role); u.setActive(true); return u; }
    Campaign campaign(int id) { Campaign c = new Campaign(); c.setId(id); c.setUser(actor(id, "CAMPAIGN_OWNER")); return c; }
    Pilgrim pilgrim(int campaignId, boolean withCard) {
        Pilgrim p = new Pilgrim(); p.setId(20); p.setCampaign(campaign(campaignId));
        if (withCard) { PilgrimCard card = new PilgrimCard(); card.setId(9); card.setCardNumber("HID-20"); card.setCardType("NUSUK"); card.setStatus("EXPIRED"); card.setImageReference("card.jpg"); p.setPilgrimCard(card); }
        when(pilgrims.findPilgrimById(20)).thenReturn(p); return p;
    }
    User staffUser(AuthorizedStaffRole role, int campaignId) {
        User u = actor(10,"AuthorizedStaff"); AuthorizedStaff a = new AuthorizedStaff(); a.setStaffRole(role); a.setCampaign(campaign(campaignId)); when(staff.findById(10)).thenReturn(Optional.of(a)); return u;
    }
    @Test void supervisorReadsAllCardFieldsWithinCampaign() {
        pilgrim(2,true); PilgrimCardLookupDTO dto = service.getCardForPilgrim(staffUser(AuthorizedStaffRole.SUPERVISOR,2),20);
        assertTrue(dto.hasCard()); assertEquals(20,dto.pilgrimId()); assertEquals(9,dto.card().id()); assertEquals("HID-20",dto.card().cardNumber()); assertEquals("NUSUK",dto.card().cardType()); assertEquals("EXPIRED",dto.card().status()); assertEquals("card.jpg",dto.card().imageReference()); verifyNoInteractions(cards);
    }
    @Test void noCardIsAnExplicitSuccessfulResult() { pilgrim(2,false); var dto=service.getCardForPilgrim(staffUser(AuthorizedStaffRole.SUPERVISOR,2),20); assertFalse(dto.hasCard()); assertNull(dto.card()); }
    @Test void representativeCanReadOwnCampaign() { pilgrim(2,true); assertTrue(service.getCardForPilgrim(staffUser(AuthorizedStaffRole.REGULAR_EMPLOYEE,2),20).hasCard()); }
    @Test void crossCampaignCardIsDenied() { pilgrim(3,true); assertThrows(AccessDeniedException.class,()->service.getCardForPilgrim(staffUser(AuthorizedStaffRole.SUPERVISOR,2),20)); }
    @Test void crossCampaignAbsenceIsAlsoDenied() { pilgrim(3,false); assertThrows(AccessDeniedException.class,()->service.getCardForPilgrim(staffUser(AuthorizedStaffRole.SUPERVISOR,2),20)); }
    @Test void ownerCanReadOwnCampaignOnly() { pilgrim(2,true); assertTrue(service.getCardForPilgrim(actor(2,"CAMPAIGN_OWNER"),20).hasCard()); assertThrows(AccessDeniedException.class,()->service.getCardForPilgrim(actor(3,"CAMPAIGN_OWNER"),20)); }
    @Test void adminCanReadCard() { pilgrim(2,true); assertTrue(service.getCardForPilgrim(actor(1,"ADMIN"),20).hasCard()); }
    @Test void fieldStaffCannotReadThisEndpoint() { pilgrim(2,true); assertThrows(AccessDeniedException.class,()->service.getCardForPilgrim(actor(8,"FIELDـSTAFF"),20)); }
    @Test void inactiveOrAnonymousActorsAreDeniedBeforeLookup() { User u=actor(2,"CAMPAIGN_OWNER");u.setActive(false);assertThrows(AccessDeniedException.class,()->service.getCardForPilgrim(u,20));assertThrows(AccessDeniedException.class,()->service.getCardForPilgrim(null,20));verifyNoInteractions(pilgrims); }
    @Test void missingPilgrimIsRejected() { assertThrows(ApiException.class,()->service.getCardForPilgrim(actor(1,"ADMIN"),99)); }
    @Test void controllerReturnsTheScopedLookup() { pilgrim(2,false); var controller = new PilgrimCardController(service); var body=(PilgrimCardLookupDTO)controller.getCardForPilgrim(actor(2,"CAMPAIGN_OWNER"),20).getBody();assertNotNull(body);assertFalse(body.hasCard()); }
}
