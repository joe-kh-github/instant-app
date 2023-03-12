package com.instantsystem.instantapp.api.parking.grandpoitiers.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GrandPoitiersParkingRecords {

    @JsonProperty("recordid")
    private String recordId;
    @JsonProperty("fields")
    private GrandPoitiersParkingFields fields;
}

