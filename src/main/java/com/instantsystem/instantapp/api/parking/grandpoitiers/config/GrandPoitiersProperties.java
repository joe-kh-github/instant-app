package com.instantsystem.instantapp.api.parking.grandpoitiers.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "parking.grandpoitiers")
@Configuration
@Data
public class GrandPoitiersProperties {

    private String baseUrl;

    private String recordSearchEndpoint;
}
