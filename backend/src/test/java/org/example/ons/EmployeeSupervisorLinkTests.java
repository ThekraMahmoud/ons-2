package org.example.ons;

import org.example.ons.Api.ApiException;
import org.example.ons.DTO.AuthorizedStaffDTO;
import org.example.ons.Enum.AuthorizedStaffRole;
import org.example.ons.Model.*;
import org.example.ons.Repository.*;
import org.example.ons.Service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.access.AccessDeniedException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeSupervisorLinkTests {
    AuthorizedStaffRepository staff;
    CampaignRepository campaigns;
    AuthRepository users;
    AuthorizedStaffService service;
    @BeforeEach void setup() {
        staff = mock(AuthorizedStaffRepository.class); campaigns = mock(CampaignRepository.class); users = mock(AuthRepository.class);
        service = new AuthorizedStaffService(staff, campaigns, users, mock(AssistanceReportRepository.class), mock(ReportStatusHistoryRepository.class), mock(MapService.class), mock(NotificationService.class));
        when(users.save(any(User.class))).thenAnswer(call -> { User saved = call.getArgument(0); saved.setId(30); return saved; });
    }
    User user(int id, String role) { User u = new User(); u.setId(id); u.setRole(role); u.setActive(true); return u; }
    Campaign campaign(int id) { Campaign c = new Campaign(); c.setId(id); c.setUser(user(id,"CAMPAIGN_OWNER")); return c; }
    AuthorizedStaff supervisor(int id,int campaignId) { AuthorizedStaff s = new AuthorizedStaff(); s.setId(id); s.setUser(user(id,"AuthorizedStaff")); s.setStaffRole(AuthorizedStaffRole.SUPERVISOR); s.setCampaign(campaign(campaignId)); return s; }
    AuthorizedStaffDTO dto(Integer supervisorId) { AuthorizedStaffDTO d = new AuthorizedStaffDTO(); d.setName("Test Delegate"); d.setEmail("delegate@example.invalid"); d.setPhoneNumber("0500000000"); d.setPassword("test-only-password"); d.setActive(true); d.setSupervisorId(supervisorId); return d; }
    void ownerCampaign() { when(campaigns.findCampaignById(2)).thenReturn(campaign(2)); }
    @Test void ownerSavesSelectedSupervisor() {
        ownerCampaign(); AuthorizedStaff leader = supervisor(8,2); when(staff.findAuthorizedStaffById(8)).thenReturn(leader);
        service.addEmployee(user(2,"CAMPAIGN_OWNER"),dto(8));
        ArgumentCaptor<AuthorizedStaff> saved = ArgumentCaptor.forClass(AuthorizedStaff.class); verify(staff).save(saved.capture());
        assertSame(leader,saved.getValue().getSupervisor()); assertEquals(2,saved.getValue().getCampaign().getId()); assertEquals(AuthorizedStaffRole.REGULAR_EMPLOYEE,saved.getValue().getStaffRole());
    }
    @Test void ownerCannotChooseAnotherCampaign() {
        ownerCampaign(); when(staff.findAuthorizedStaffById(8)).thenReturn(supervisor(8,99));
        assertThrows(AccessDeniedException.class,()->service.addEmployee(user(2,"CAMPAIGN_OWNER"),dto(8))); verify(users,never()).save(any()); verify(staff,never()).save(any());
    }
    @Test void ownerMustSelectSupervisor() {
        ownerCampaign(); assertThrows(ApiException.class,()->service.addEmployee(user(2,"CAMPAIGN_OWNER"),dto(null))); verify(users,never()).save(any());
    }
    @Test void missingSupervisorIsDenied() {
        ownerCampaign(); assertThrows(AccessDeniedException.class,()->service.addEmployee(user(2,"CAMPAIGN_OWNER"),dto(404))); verify(users,never()).save(any());
    }
    @Test void inactiveSupervisorIsDenied() {
        ownerCampaign(); AuthorizedStaff leader=supervisor(8,2); leader.getUser().setActive(false); when(staff.findAuthorizedStaffById(8)).thenReturn(leader);
        assertThrows(AccessDeniedException.class,()->service.addEmployee(user(2,"CAMPAIGN_OWNER"),dto(8))); verify(users,never()).save(any());
    }
    @Test void delegateCannotBeSelectedAsSupervisor() {
        ownerCampaign(); AuthorizedStaff leader=supervisor(8,2); leader.setStaffRole(AuthorizedStaffRole.REGULAR_EMPLOYEE); when(staff.findAuthorizedStaffById(8)).thenReturn(leader);
        assertThrows(AccessDeniedException.class,()->service.addEmployee(user(2,"CAMPAIGN_OWNER"),dto(8))); verify(users,never()).save(any());
    }
    @Test void supervisorAutomaticallyGetsTheirOwnEmployee() {
        AuthorizedStaff leader=supervisor(8,2); when(staff.findAuthorizedStaffById(8)).thenReturn(leader);
        service.addEmployee(user(8,"AuthorizedStaff"),dto(null));
        ArgumentCaptor<AuthorizedStaff> saved=ArgumentCaptor.forClass(AuthorizedStaff.class); verify(staff).save(saved.capture()); assertSame(leader,saved.getValue().getSupervisor());
    }
    @Test void supervisorCannotAssignToDifferentSupervisor() {
        when(staff.findAuthorizedStaffById(8)).thenReturn(supervisor(8,2));
        assertThrows(AccessDeniedException.class,()->service.addEmployee(user(8,"AuthorizedStaff"),dto(9))); verify(users,never()).save(any());
    }
}
