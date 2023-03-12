package com.instantsystem.instantapp.services;


import com.instantsystem.instantapp.api.ParkingApiService;
import com.instantsystem.instantapp.exceptions.InstantException;
import com.instantsystem.instantapp.dto.ParkingDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * The type Parking service.
 */
@Service
public class ParkingService {

    private final Logger logger = LoggerFactory.getLogger(ParkingService.class);

    private final ParkingApiService parkingApiService;

    /**
     * Instantiates a new Parking service.
     *
     * @param parkingApiService the parking api service
     */
    public ParkingService(ParkingApiService parkingApiService) {
        this.parkingApiService = parkingApiService;
    }

    /**
     * Gets parkings.
     *
     * @param params the params
     * @return the parkings
     */
    public List<ParkingDto> getParkings(Map<String, Object> params) {
        try {
            return this.parkingApiService.getParkings(params);
        } catch (InstantException instantException) {
            logger.error(instantException.getMessage(), instantException);
            throw new InstantException(instantException.getMessage(), instantException.getHttpStatus(), instantException);
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
            throw new InstantException(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, exception);
        }
    }
}
