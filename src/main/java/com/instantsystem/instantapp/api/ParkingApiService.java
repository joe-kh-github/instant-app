package com.instantsystem.instantapp.api;

import com.instantsystem.instantapp.exceptions.InstantException;
import com.instantsystem.instantapp.dto.ParkingDto;

import java.util.List;
import java.util.Map;

public interface ParkingApiService {

    List<ParkingDto> getParkings(Map<String, Object> params) throws InstantException;

}
