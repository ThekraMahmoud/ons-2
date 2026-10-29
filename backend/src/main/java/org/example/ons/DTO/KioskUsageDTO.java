
package org.example.ons.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KioskUsageDTO {

    private String kioskCode;
    private String locationName;
    private String status;
    private long totalReports;
}
