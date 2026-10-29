package org.example.ons.DTO;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MergeReportsDTO {

    @NotNull(message = "Staff ID is required")
    @Positive
    private Integer staffId;

    @NotEmpty(message = "Additional report IDs are required")
    private List<@NotNull @Positive Long> reportIds;
}