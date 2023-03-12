package com.instantsystem.instantapp.api.parking.grandpoitiers.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GrandPoitiersParking {
    @JsonProperty("records")
    private List<GrandPoitiersParkingRecords> records;
}