package com.instantsystem.instantapp.controllers;

import com.instantsystem.instantapp.dto.ParkingDto;
import com.instantsystem.instantapp.services.ParkingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * The type Parking controller.
 */
@RestController
@RequestMapping("/parkings")
public class ParkingController {


    private final ParkingService parkingService;

    /**
     * Instantiates a new Parking controller.
     *
     * @param parkingService the parking service
     */
    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }


    /**
     * Gets parkings.
     *
     * @param address   the address
     * @param latitude  the latitude
     * @param longitude the longitude
     * @param distance  the distance
     * @return the parkings
     */
    @GetMapping
    public ResponseEntity<List<ParkingDto>> getParkings(
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude,
            @RequestParam(value = "distance", required = false) Double distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("address", address);
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        params.put("distance", distance);

        List<ParkingDto> parkingDtos = this.parkingService.getParkings(params);
        return ResponseEntity.ok(parkingDtos);
    }


}
