package com.instantsystem.instantapp.api.parking.grandpoitiers.mappers;

import com.instantsystem.instantapp.api.parking.grandpoitiers.models.GrandPoitiersParkingRecords;
import com.instantsystem.instantapp.dto.ParkingDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GrandPoitiersParkingMapper {

    @Mapping(target = "id", source = "recordId")
    @Mapping(source = "fields.name", target = "name")
    @Mapping(source = "fields.address", target = "address")
    @Mapping(source = "fields.coordinates", target = "coordinates")
    @Mapping(source = "fields.capacite", target = "capacite")
    @Mapping(source = "fields.places", target = "places")
    ParkingDto toParking(GrandPoitiersParkingRecords grandPoitiersParkingRecords);
}
