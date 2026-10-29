
package org.example.ons.Service;

import lombok.AllArgsConstructor;
import org.example.ons.Api.ApiException;
import org.example.ons.DTO.KioskActivityDTO;
import org.example.ons.DTO.KioskUsageDTO;
import org.example.ons.Model.AssistanceReport;
import org.example.ons.Model.Kiosk;
import org.example.ons.Repository.AssistanceReportRepository;
import org.example.ons.Repository.KioskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class KioskService {

    private final KioskRepository kioskRepository;
    private final AssistanceReportRepository assistanceReportRepository;

    // Create Kiosk
    public void createKiosk(Kiosk kiosk) {
        kioskRepository.save(kiosk);
    }

    // Get All Kiosks
    public List<Kiosk> getAllKiosks() {
        return kioskRepository.findAll();
    }

    // Get Kiosk By ID
    public Kiosk getKioskById(Integer id) {
        Kiosk kiosk = kioskRepository.findKioskById(id);

        if (kiosk == null) {
            throw new ApiException("Kiosk not found with ID: " + id);
        }

        return kiosk;
    }

    // Update Kiosk
    public void updateKiosk(Integer id, Kiosk updatedKiosk) {

        Kiosk existingKiosk = getKioskById(id);

        existingKiosk.setKioskCode(updatedKiosk.getKioskCode());
        existingKiosk.setLocationName(updatedKiosk.getLocationName());
        existingKiosk.setLatitude(updatedKiosk.getLatitude());
        existingKiosk.setLongitude(updatedKiosk.getLongitude());
        existingKiosk.setStatus(updatedKiosk.getStatus());

        kioskRepository.save(existingKiosk);
    }

    // Delete Kiosk
     public void deleteKiosk(Integer id) {
        Kiosk kiosk = getKioskById(id);
        kioskRepository.delete(kiosk);
    }




    // Update Kiosk Status
    public void updateKioskStatus(Integer id, String status) {

        Kiosk kiosk = getKioskById(id);

        if (status == null || !status.matches("ACTIVE|INACTIVE|MAINTENANCE")) {
            throw new ApiException("Status must be ACTIVE, INACTIVE, or MAINTENANCE");
        }
        kiosk.setStatus(status);
        kioskRepository.save(kiosk);
    }



    public void updateLocation(Integer kioskId, Kiosk kiosk) {

        Kiosk kioskLocation = kioskRepository.findKioskById(kioskId);

        if (kioskLocation == null) {
            throw new ApiException("Kiosk not found");
        }
        kioskLocation.setLatitude(kiosk.getLatitude());
        kioskLocation.setLongitude(kiosk.getLongitude());

        kioskRepository.save(kioskLocation);
    }


    //تسوي تحفظ اخر وقت لارسال الرساله
    public void updateAllKiosksHeartbeat() {

        List<Kiosk> kiosks = kioskRepository.findAll();

        for (Kiosk kiosk : kiosks) {
            kiosk.setLastHeartbeat(LocalDateTime.now());
        }

        kioskRepository.saveAll(kiosks);
    }


    public List<AssistanceReport> getAllReportForKiosk(Integer kioskId){
        Kiosk kiosk=kioskRepository.findKioskById(kioskId);

        if(kiosk==null){
            throw new ApiException("kiosk not found");
        }
        return assistanceReportRepository.findAssistanceReportByKiosk(kiosk);
    }



    //يجيب تقرير عن كل الاجهزه الموجوده في النظام من النشطه وغيرها
    //ياخذ اثنين dto  واحد يعرض بيانات الاجهزه النشطه وتفاصيلها والثاني ياخي الاحهزه الثانيه كم عددها
    public KioskActivityDTO getAllActivityDto() {

        List<Kiosk> kiosks = kioskRepository.findAll();

        long active = 0;
        long inactive = 0;
        long maintenance = 0;

        List<KioskUsageDTO> devicesActivity = new ArrayList<>();

        for (Kiosk kiosk : kiosks) {

            if ("ACTIVE".equals(kiosk.getStatus())) {
                active++;
            } else if ("INACTIVE".equals(kiosk.getStatus())) {
                inactive++;
            } else if ("MAINTENANCE".equals(kiosk.getStatus())) {
                maintenance++;
            }

            KioskUsageDTO dto = new KioskUsageDTO();
            dto.setKioskCode(kiosk.getKioskCode());
            dto.setLocationName(kiosk.getLocationName());
            dto.setStatus(kiosk.getStatus());
            dto.setTotalReports(assistanceReportRepository.countByKiosk(kiosk));
            devicesActivity.add(dto);
        }

        KioskActivityDTO result = new KioskActivityDTO();

        result.setTotalDevices(kiosks.size());
        result.setActiveDevices(active);
        result.setInactiveDevices(inactive);
        result.setMaintenanceDevices(maintenance);
        result.setDevicesActivity(devicesActivity);

        return result;
    }


}




