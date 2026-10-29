package org.example.ons.DTO;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RouteRequestDTO {

    @NotNull
    @DecimalMin("-90")
    @DecimalMax("90")
    private Double originLatitude;

    @NotNull
    @DecimalMin("-180")
    @DecimalMax("180")
    private Double originLongitude;

    @NotNull
    @DecimalMin("-90")
    @DecimalMax("90")
    private Double destinationLatitude;

    @NotNull
    @DecimalMin("-180")
    @DecimalMax("180")
    private Double destinationLongitude;
}