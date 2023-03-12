package com.instantsystem.instantapp.api.parking.grandpoitiers.impl;

import com.instantsystem.instantapp.api.ParkingApiService;
import com.instantsystem.instantapp.api.parking.grandpoitiers.config.GrandPoitiersProperties;
import com.instantsystem.instantapp.api.parking.grandpoitiers.mappers.GrandPoitiersParkingMapper;
import com.instantsystem.instantapp.api.parking.grandpoitiers.models.GrandPoitiersParking;
import com.instantsystem.instantapp.api.utils.Utils;
import com.instantsystem.instantapp.dto.ParkingDto;
import com.instantsystem.instantapp.exceptions.ErrorMessages;
import com.instantsystem.instantapp.exceptions.InstantException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GrandPoitiersParkingImpl implements ParkingApiService {

    private static final String PARKING_DATESET = "mobilite-parkings-grand-poitiers-donnees-metiers";
    private static final String PARKING_DATESET_REALTIME = "mobilites-stationnement-des-parkings-en-temps-reel";

    private final RestTemplate restTemplate;

    private final GrandPoitiersProperties grandPoitiersProperties;

    private final GrandPoitiersParkingMapper grandPoitiersParkingMapper;

    public GrandPoitiersParkingImpl(RestTemplate restTemplate,
                                    GrandPoitiersProperties grandPoitiersProperties,
                                    GrandPoitiersParkingMapper grandPoitiersParkingMapper) {
        this.restTemplate = restTemplate;
        this.grandPoitiersProperties = grandPoitiersProperties;
        this.grandPoitiersParkingMapper = grandPoitiersParkingMapper;
    }

    @Override
    public List<ParkingDto> getParkings(Map<String, Object> params) {
        Map<String, Object> requestParams = new HashMap<>();

        // remplir les paramètres
        prepareRequestParams(params, requestParams);

        // appel aux deux datasets
        List<ParkingDto> parkingDtos = searchParking(PARKING_DATESET, requestParams);
        List<ParkingDto> vacantParkingDtos = searchParking(PARKING_DATESET_REALTIME, requestParams);

        // vérifier les places libres
        vacantParkingDtos = vacantParkingDtos.stream().filter(p -> p.getPlaces() < p.getCapacite()).toList();

        // merger les objets qui sont identiques
        parkingDtos = mergeIdenticalObjects(parkingDtos, vacantParkingDtos);

        return parkingDtos;
    }

    private void prepareRequestParams(Map<String, Object> params, Map<String, Object> requestParams) {


        if (Utils.hasKeyAndValidValue(params, "latitude") && Utils.hasKeyAndValidValue(params, "longitude")) {
            Object distance = params.get("distance");
            String geoDistanceParam = String.format("%s,%s%s", params.get("latitude"), params.get("longitude"),
                    distance != null ? ("," + distance) : "");
            requestParams.put("geofilter.distance", geoDistanceParam);
        } else if (Utils.hasKeyAndValidValue(params, "address")) {
            requestParams.put("refine.adresse", params.get("address"));
        }
    }

    private List<ParkingDto> mergeIdenticalObjects(List<ParkingDto> parkingDtos, List<ParkingDto> vacantParkingDtos) {
        return parkingDtos.stream().filter(parking -> {
            Optional<ParkingDto> vacantParking = vacantParkingDtos.stream()
                    .filter(vp -> vp.getName().equals(parking.getName()))
                    .findFirst();

            if (vacantParking.isPresent()) {
                parking.setCapacite(vacantParking.get().getCapacite());
                parking.setPlaces(vacantParking.get().getPlaces());
                return true;
            }
            return false;
        }).toList();
    }

    private List<ParkingDto> searchParking(String dataset, Map<String, Object> params) throws InstantException {
        URI uri = buildUri(dataset, params);
        HttpHeaders headers = buildHeaders();
        HttpEntity<GrandPoitiersParking> entity = new HttpEntity<>(headers);

        ResponseEntity<GrandPoitiersParking> response = restTemplate.exchange(uri, HttpMethod.GET, entity, GrandPoitiersParking.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new InstantException(ErrorMessages.ERROR_CALLING_RECORDS_API, HttpStatus.valueOf(response.getStatusCode().value()));
        }

        List<ParkingDto> parkingDtos = new ArrayList<>();
        if (response.getBody() != null && response.getBody().getRecords() != null) {
            response.getBody().getRecords().forEach(parkingRecord -> parkingDtos.add(this.grandPoitiersParkingMapper.toParking(parkingRecord)));
        }
        return parkingDtos;
    }

    private URI buildUri(String dataset, Map<String, Object> params) {
        var builder = UriComponentsBuilder.fromUriString(
                        this.grandPoitiersProperties.getBaseUrl() +
                                this.grandPoitiersProperties.getRecordSearchEndpoint())
                .queryParam("dataset", dataset);
        params.forEach(builder::queryParam);

        return builder.encode(Charset.defaultCharset()).build().toUri();
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }


}
