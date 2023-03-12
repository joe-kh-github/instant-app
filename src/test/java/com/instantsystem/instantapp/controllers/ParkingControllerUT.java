package com.instantsystem.instantapp.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.instantsystem.instantapp.api.parking.grandpoitiers.models.GrandPoitiersParking;
import com.instantsystem.instantapp.api.parking.grandpoitiers.models.GrandPoitiersParkingFields;
import com.instantsystem.instantapp.api.parking.grandpoitiers.models.GrandPoitiersParkingRecords;
import com.instantsystem.instantapp.dto.ParkingDto;
import com.instantsystem.instantapp.services.ParkingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ParkingControllerUT {

    @Autowired
    private ParkingService parkingService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void givenAddress_whenCallingGetParkings_thenReturnParkings() throws Exception {
        // given
        String name = "PALAIS DE JUSTICE";
        String address = "Boulevard De Lattre de Tassigny";
        Integer capacity = 70;
        Integer places = 68;
        List<Double> coordinates = List.of(46.58595804860371, 0.3512954265806957);

        // when
        mockBothDataSetResponse(name, address, capacity, places, coordinates);

        MvcResult mvcResult = mockMvc.perform(get("/parkings")
                        .param("address", address))
                .andExpect(status().isOk())
                .andReturn();

        // then
        String contentAsString = mvcResult.getResponse().getContentAsString();
        List<ParkingDto> parkingRes = objectMapper.readValue(contentAsString, new TypeReference<>() {
        });
        verifyAttributes(name, address, capacity, places, coordinates, parkingRes);
    }

    @Test
    void givenCoordinates_whenCallingGetParkings_thenReturnParkings() throws Exception {
        // given
        String name = "PALAIS DE JUSTICE";
        String address = "Boulevard De Lattre de Tassigny";
        Integer capacity = 70;
        Integer places = 68;
        Double latitude = 46.58595804860371d;
        Double longitude = 0.3512954265806957d;
        double distance = 2d;
        List<Double> coordinates = List.of(latitude, longitude);

        // when
        mockBothDataSetResponse(name, address, capacity, places, coordinates);

        MvcResult mvcResult = mockMvc.perform(get("/parkings")
                        .param("latitude", latitude.toString())
                        .param("longitude", longitude.toString())
                        .param("distance", Double.toString(distance)))
                .andExpect(status().isOk())
                .andReturn();

        // then
        String contentAsString = mvcResult.getResponse().getContentAsString();
        List<ParkingDto> parkingRes = objectMapper.readValue(contentAsString, new TypeReference<>() {
        });
        verifyAttributes(name, address, capacity, places, coordinates, parkingRes);
    }

    @Test
    void givenCoordinates_whenCallingGetParkingsAndRestException_thenThrowInstantException() throws Exception {
        // given
        double latitude = 46.58595804860371d;
        double longitude = 0.3512954265806957d;
        double distance = 2d;
        RestClientException restClientException = new RestClientException("Une erreur I/O");

        // when
        Mockito.doThrow(restClientException).when(restTemplate).exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), eq(GrandPoitiersParking.class));


        MvcResult mvcResult = mockMvc.perform(get("/parkings")
                        .param("latitude", Double.toString(latitude))
                        .param("longitude", Double.toString(longitude))
                        .param("distance", Double.toString(distance)))
                .andExpect(status().is5xxServerError())
                .andReturn();

        // then
        String contentAsString = mvcResult.getResponse().getContentAsString();
        Map<String, Object> map = objectMapper.readValue(contentAsString, new TypeReference<>() {
        });

        assertEquals(2, map.size());
        assertEquals(restClientException.getMessage(), map.get("message"));
        assertEquals(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), map.get("code"));
    }

    private void mockBothDataSetResponse(String name,
                                         String address,
                                         Integer capacity,
                                         Integer places,
                                         List<Double> coordinates) {
        ResponseEntity<GrandPoitiersParking> parkings =
                mockGrandPoitiersParkingResponse("e24d0474ce8282db403e8cbf61c94069c1b89460",
                        name,
                        address,
                        null,
                        null,
                        coordinates);
        ResponseEntity<GrandPoitiersParking> vacantParkings =
                mockGrandPoitiersParkingResponse("a54d0474ce8282db403e8cbf61c94069c1b89909",
                        name,
                        null,
                        capacity,
                        places,
                        coordinates);
        // when
        Mockito.doReturn(parkings, vacantParkings).when(restTemplate).exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), eq(GrandPoitiersParking.class));
    }

    private static void verifyAttributes(String name,
                                         String address,
                                         Integer capacity,
                                         Integer places,
                                         List<Double> coordinates,
                                         List<ParkingDto> parkingRes) {
        assertEquals(1, parkingRes.size());
        assertEquals(name, parkingRes.get(0).getName());
        assertEquals(address, parkingRes.get(0).getAddress());
        assertEquals(capacity, parkingRes.get(0).getCapacite());
        assertEquals(places, parkingRes.get(0).getPlaces());
        assertEquals(coordinates, parkingRes.get(0).getCoordinates());
    }

    private ResponseEntity<GrandPoitiersParking> mockGrandPoitiersParkingResponse(String recordId,
                                                                                  String name,
                                                                                  String address,
                                                                                  Integer capacity,
                                                                                  Integer places,
                                                                                  List<Double> coordinates) {
        GrandPoitiersParking grandPoitiersParking = new GrandPoitiersParking();
        List<GrandPoitiersParkingRecords> records = new ArrayList<>();
        GrandPoitiersParkingRecords grandPoitiersParkingRecords = new GrandPoitiersParkingRecords();
        GrandPoitiersParkingFields grandPoitiersParkingFields = new GrandPoitiersParkingFields();

        grandPoitiersParkingRecords.setRecordId(recordId);
        grandPoitiersParkingFields.setName(name);
        grandPoitiersParkingFields.setAddress(address);
        grandPoitiersParkingFields.setCapacite(capacity);
        grandPoitiersParkingFields.setPlaces(places);
        grandPoitiersParkingFields.setCoordinates(coordinates);

        grandPoitiersParkingRecords.setFields(grandPoitiersParkingFields);
        records.add(grandPoitiersParkingRecords);
        grandPoitiersParking.setRecords(records);

        return new ResponseEntity<>(grandPoitiersParking, HttpStatus.OK);
    }
}
