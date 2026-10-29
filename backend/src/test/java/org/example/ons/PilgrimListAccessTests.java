package org.example.ons;

import org.example.ons.Enum.AuthorizedStaffRole;
import org.example.ons.Model.*;
import org.example.ons.Repository.*;
import org.example.ons.Service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PilgrimListAccessTests {
    PilgrimRepository pilgrims;
    CampaignRepository campaigns;
    AuthorizedStaffService staffService;
    PilgrimService service;

    @BeforeEach void setup() {
        pilgrims = mock(PilgrimRepository.class);
        campaigns = mock(CampaignRepository.class);
        staffService = mock(AuthorizedStaffService.class);
        service = new PilgrimService(pilgrims, campaigns,
                mock(AssistanceReportRepository.class), mock(AccommodationRepository.class),
                staffService, mock(PilgrimCardService.class));
    }
    User user(int id, String role) {
        User user = new User(); user.setId(id); user.setRole(role); user.setActive(true); return user;
    }
    Campaign campaign(int id) {
        Campaign campaign = new Campaign(); campaign.setId(id); campaign.setUser(user(id, "CAMPAIGN_OWNER")); return campaign;
    }
    @Test void adminReadsAllPilgrims() {
        List<Pilgrim> expected = List.of(new Pilgrim(), new Pilgrim());
        when(pilgrims.findAll()).thenReturn(expected);
        assertSame(expected, service.getAllPilgrims(user(1, "ADMIN")));
        verify(pilgrims, never()).findByCampaign_Id(anyInt());
    }
    @Test void ownerReadsOwnCampaignOnly() {
        when(campaigns.findCampaignById(2)).thenReturn(campaign(2));
        List<Pilgrim> expected = List.of(new Pilgrim());
        when(pilgrims.findByCampaign_Id(2)).thenReturn(expected);
        assertSame(expected, service.getAllPilgrims(user(2, "CAMPAIGN_OWNER")));
        verify(pilgrims, never()).findAll();
    }
    void staffReadsOwnCampaign(AuthorizedStaffRole role) {
        AuthorizedStaff staff = new AuthorizedStaff(); staff.setCampaign(campaign(2)); staff.setStaffRole(role);
        when(staffService.getStaffById(10)).thenReturn(staff);
        List<Pilgrim> expected = List.of(new Pilgrim());
        when(pilgrims.findByCampaign_Id(2)).thenReturn(expected);
        assertSame(expected, service.getAllPilgrims(user(10, "AuthorizedStaff")));
        verify(pilgrims, never()).findAll();
    }
    @Test void supervisorReadsOwnCampaignOnly() { staffReadsOwnCampaign(AuthorizedStaffRole.SUPERVISOR); }
    @Test void representativeReadsOwnCampaignOnly() { staffReadsOwnCampaign(AuthorizedStaffRole.REGULAR_EMPLOYEE); }
    @Test void fieldStaffCannotReadFullList() {
        assertThrows(AccessDeniedException.class, () -> service.getAllPilgrims(user(8, "FIELDـSTAFF")));
        verifyNoInteractions(pilgrims);
    }
    @Test void unauthenticatedCannotReadList() {
        assertThrows(AccessDeniedException.class, () -> service.getAllPilgrims(null)); verifyNoInteractions(pilgrims);
    }
    @Test void inactiveAccountCannotReadList() {
        User actor = user(1, "ADMIN"); actor.setActive(false);
        assertThrows(AccessDeniedException.class, () -> service.getAllPilgrims(actor)); verifyNoInteractions(pilgrims);
    }
    @Test void ownerWithoutCampaignIsDenied() {
        assertThrows(AccessDeniedException.class, () -> service.getAllPilgrims(user(9, "CAMPAIGN_OWNER"))); verifyNoInteractions(pilgrims);
    }
    @Test void staffWithoutCampaignIsDenied() {
        when(staffService.getStaffById(10)).thenReturn(new AuthorizedStaff());
        assertThrows(AccessDeniedException.class, () -> service.getAllPilgrims(user(10, "AuthorizedStaff"))); verifyNoInteractions(pilgrims);
    }
}
