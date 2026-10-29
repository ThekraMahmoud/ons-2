package org.example.ons;

import jakarta.persistence.EntityManager;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import org.example.ons.Api.ApiException;
import org.example.ons.Enum.AuthorizedStaffRole;
import org.example.ons.Model.*;
import org.example.ons.Repository.*;
import org.example.ons.Service.PilgrimCardService;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PilgrimCardIdentityTests {
    final PilgrimCardRepository cards=mock(PilgrimCardRepository.class);
    final PilgrimRepository pilgrims=mock(PilgrimRepository.class);
    final AuthorizedStaffRepository staff=mock(AuthorizedStaffRepository.class);
    final EntityManager manager=mock(EntityManager.class);
    final PilgrimCardService service=new PilgrimCardService(cards,pilgrims,staff);
    PilgrimCardIdentityTests(){ReflectionTestUtils.setField(service,"entityManager",manager);}
    User supervisor(){User u=new User();u.setId(10);u.setRole("AuthorizedStaff");u.setActive(true);return u;}
    Pilgrim prepare(int campaignId, AuthorizedStaffRole role){
        Campaign campaign=new Campaign();campaign.setId(campaignId);Pilgrim p=new Pilgrim();p.setId(20);p.setCampaign(campaign);when(pilgrims.findPilgrimById(20)).thenReturn(p);
        Campaign own=new Campaign();own.setId(2);AuthorizedStaff a=new AuthorizedStaff();a.setCampaign(own);a.setStaffRole(role);when(staff.findById(10)).thenReturn(Optional.of(a));return p;
    }
    PilgrimCard newCard(){PilgrimCard c=new PilgrimCard();c.setCardNumber("CARD-TEST");c.setCardType("NUSUK");c.setStatus("ACTIVE");return c;}
    @Test void modelUsesIdentityGeneration() throws Exception {var annotation=PilgrimCard.class.getDeclaredField("Id").getAnnotation(GeneratedValue.class);assertNotNull(annotation);assertEquals(GenerationType.IDENTITY,annotation.strategy());}
    @Test void createWithoutManualIdPersistsTheSelectedPilgrim(){Pilgrim p=prepare(2,AuthorizedStaffRole.SUPERVISOR);PilgrimCard c=newCard();service.createPilgrimCard(supervisor(),20,c);assertNull(c.getId());assertSame(p,c.getPilgrim());verify(manager).persist(c);verifyNoInteractions(cards);}
    @Test void suppliedIdCannotOverwriteAnExistingCard(){prepare(2,AuthorizedStaffRole.SUPERVISOR);PilgrimCard c=newCard();c.setId(12345);doAnswer(call->{assertNull(((PilgrimCard)call.getArgument(0)).getId());return null;}).when(manager).persist(c);service.createPilgrimCard(supervisor(),20,c);verify(manager).persist(c);verify(manager,never()).merge(any());verifyNoInteractions(cards);}
    @Test void existingPilgrimCardStillBlocksDuplicate(){Pilgrim p=prepare(2,AuthorizedStaffRole.SUPERVISOR);p.setPilgrimCard(newCard());assertThrows(ApiException.class,()->service.createPilgrimCard(supervisor(),20,newCard()));verifyNoInteractions(manager);}
    @Test void anotherCampaignCannotCreateCard(){prepare(3,AuthorizedStaffRole.SUPERVISOR);assertThrows(AccessDeniedException.class,()->service.createPilgrimCard(supervisor(),20,newCard()));verifyNoInteractions(manager);}
    @Test void representativeCannotCreateCard(){prepare(2,AuthorizedStaffRole.REGULAR_EMPLOYEE);assertThrows(AccessDeniedException.class,()->service.createPilgrimCard(supervisor(),20,newCard()));verifyNoInteractions(manager);}
    @Test void inactiveOrAnonymousCannotCreateCard(){User u=supervisor();u.setActive(false);assertThrows(AccessDeniedException.class,()->service.createPilgrimCard(u,20,newCard()));assertThrows(AccessDeniedException.class,()->service.createPilgrimCard(null,20,newCard()));verifyNoInteractions(manager,pilgrims);}
}
