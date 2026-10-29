package org.example.ons.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
@Data
@Getter
@Setter
public class KioskActivityDTO {

    private long totalDevices;
    private long activeDevices;
    private long inactiveDevices;
    private long maintenanceDevices;
    private List<KioskUsageDTO> devicesActivity;
}