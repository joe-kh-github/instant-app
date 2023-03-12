package com.instantsystem.instantapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ParkingDto {
    @JsonProperty("id")
    private String id;

    @JsonProperty("nom")
    private String name;

    @JsonProperty("adresse")
    private String address;

    @JsonProperty("capacite")
    private Integer capacite;

    @JsonProperty("places")
    private Integer places;

    @JsonProperty("geo_point_2d")
    private List<Double> coordinates;
}