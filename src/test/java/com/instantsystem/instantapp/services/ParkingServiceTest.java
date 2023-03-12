package com.instantsystem.instantapp.services;

import com.instantsystem.instantapp.api.ParkingApiService;
import com.instantsystem.instantapp.dto.ParkingDto;
import com.instantsystem.instantapp.exceptions.ErrorMessages;
import com.instantsystem.instantapp.exceptions.InstantException;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientException;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {

    EasyRandomParameters parameters = new EasyRandomParameters()
            .seed(123L)
            .objectPoolSize(100)
            .randomizationDepth(3)
            .charset(StandardCharsets.UTF_8)
            .stringLengthRange(5, 50)
            .collectionSizeRange(1, 10)
            .scanClasspathForConcreteTypes(true)
            .overrideDefaultInitialization(false)
            .ignoreRandomizationErrors(true);
    private final EasyRandom easyRandom = new EasyRandom(parameters);

    @Mock
    private ParkingApiService parkingApiService;

    @InjectMocks
    private ParkingService parkingService;

    @Test
    void givenParams_whenGetParkings_thenReturnParkings() {

        // given
        List<ParkingDto> parkingDtos = List.of(mockParkingDto());

        // when
        Mockito.doReturn(parkingDtos).when(parkingApiService).getParkings(any());
        List<ParkingDto> parkings = this.parkingService.getParkings(new HashMap<>());

        // then
        assertNotNull(parkings);
    }

    @Test
    void givenParams_whenGetParkingsAndBadRequest_thenThrowInstantException() {
        // given
        InstantException instantException = new InstantException(ErrorMessages.ERROR_CALLING_RECORDS_API, HttpStatus.FORBIDDEN);

        // when
        doThrow(instantException).when(parkingApiService).getParkings(any());

        InstantException exception = assertThrows(InstantException.class, () -> {
            this.parkingService.getParkings(new HashMap<>());
        });

        // then
        assertEquals(instantException.getMessage(), exception.getMessage());
        assertEquals(instantException.getHttpStatus(), exception.getHttpStatus());
    }

    @Test
    void givenParams_whenGetParkingsAndRestException_thenThrowGenericException() {
        // given
        RestClientException restClientException = new RestClientException("Une erreur est survenue lors de l'appel au service rest");

        // when
        doThrow(restClientException).when(parkingApiService).getParkings(any());

        InstantException instantException = assertThrows(InstantException.class, () -> {
            this.parkingService.getParkings(new HashMap<>());
        });

        // then
        assertEquals(restClientException.getMessage(), instantException.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, instantException.getHttpStatus());
    }

    private ParkingDto mockParkingDto() {
        return easyRandom.nextObject(ParkingDto.class);
    }


}