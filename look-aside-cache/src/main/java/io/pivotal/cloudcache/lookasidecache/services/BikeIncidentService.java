package io.pivotal.cloudcache.lookasidecache.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.cloudcache.lookasidecache.domain.BikeIncident;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Service
public class BikeIncidentService {
    private final RestTemplate restTemplate;

    @Value("${bikewise.api.url}")
    private String API_URL;

    public BikeIncidentService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Cacheable("BikeIncidentsByZip")
    public List<BikeIncident> getBikeIncidents(String zipCode) throws IOException {

        String jsonIncidents = restTemplate.getForObject(API_URL + zipCode, String.class);

        return convertJsonToBikeIncidents(jsonIncidents);
    }

    private List<BikeIncident> convertJsonToBikeIncidents(String jsonIncidents) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        JsonNode bikeIncidentsAsJsonNode = objectMapper.readTree(jsonIncidents);

        List<BikeIncident> bikeIncidents = objectMapper.convertValue(bikeIncidentsAsJsonNode.get("incidents"),
                new TypeReference<List<BikeIncident>>(){});

        return bikeIncidents;
    }
}
