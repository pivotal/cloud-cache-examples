/*
Copyright (C) 2019-Present Pivotal Software, Inc. All rights reserved.

This program and the accompanying materials are made available under the terms of the under the Apache License, Version
2.0 (the "License”); you may not use this file except in compliance with the License. You may obtain a copy of the
License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
language governing permissions and limitations under the License.
 */

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
