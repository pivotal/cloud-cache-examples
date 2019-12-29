/*Copyright (C) 2019-Present Pivotal Software, Inc. All rights reserved.

This program and the accompanying materials are made available under the terms of the under the Apache License, Version
2.0 (the "License”); you may not use this file except in compliance with the License. You may obtain a copy of the
License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
language governing permissions and limitations under the License.*/

package io.pivotal.cloudcache.lookasidecache.services;

import io.pivotal.cloudcache.lookasidecache.LookAsideCacheApplication;
import io.pivotal.cloudcache.lookasidecache.domain.BikeIncident;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestGatewaySupport;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = LookAsideCacheApplication.class)
public class BikeIncidentServiceTest {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    BikeIncidentService bikeIncidentService;

    @Autowired
    CacheManager cacheManager;

    private MockRestServiceServer mockRestServer;

    @Value("${bikewise.api.url}")
    private String API_URL;

    private final String ZIP_CODE_30306 = "30306";
    private final String ZIP_CODE_97007 = "97007";

    private String mockIncidentsJsonForZipcode_30306;
    private String mockIncidentsJsonForZipcode_97007;

    @Before
    public void setUp() throws IOException {
        RestGatewaySupport gateway = new RestGatewaySupport();
        gateway.setRestTemplate(restTemplate);
        mockRestServer = MockRestServiceServer.createServer(gateway);

        File file_30306 =
                ResourceUtils.getFile("src/test/java/resources/mockIncidentsJsonForZipcode_30306.json");
        mockIncidentsJsonForZipcode_30306 = new String(Files.readAllBytes(file_30306.toPath()));
        File file_97007 =
                ResourceUtils.getFile("src/test/java/resources/mockIncidentsJsonForZipcode_97007.json");
        mockIncidentsJsonForZipcode_97007 = new String(Files.readAllBytes(file_97007.toPath()));
    }

    @Test

    public void getBikeIncidents_ShouldReturnIncidentsOccurringWithinSpecifiedZipCode_GivenZipcode()
            throws IOException {
        mockRestServer.expect(ExpectedCount.once(), requestTo(API_URL + ZIP_CODE_30306))
                .andRespond(withSuccess(mockIncidentsJsonForZipcode_30306, MediaType.APPLICATION_JSON));
        mockRestServer.expect(ExpectedCount.once(), requestTo(API_URL + ZIP_CODE_97007))
                .andRespond(withSuccess(mockIncidentsJsonForZipcode_97007, MediaType.APPLICATION_JSON));

        List<BikeIncident> resultsFor_30306 =
                bikeIncidentService.getBikeIncidents(ZIP_CODE_30306);
        List<BikeIncident> resultsFor_97007 =
                bikeIncidentService.getBikeIncidents(ZIP_CODE_97007);

        mockRestServer.verify();
        assertEquals(4, resultsFor_30306.size());
        assertEquals(1, resultsFor_97007.size());
    }

    @Test
    @DirtiesContext
    public void getBikeIncidents_ShouldPullFromCache_AfterFirstResult() throws IOException {
        mockRestServer.expect(ExpectedCount.once(), requestTo(API_URL + ZIP_CODE_30306))
                .andRespond(withSuccess(mockIncidentsJsonForZipcode_30306, MediaType.APPLICATION_JSON));

        List<BikeIncident> resultsFor_30306_fromApi =
                bikeIncidentService.getBikeIncidents(ZIP_CODE_30306);
        List<BikeIncident> resultsFor_30306_fromCache =
                bikeIncidentService.getBikeIncidents(ZIP_CODE_30306);

        mockRestServer.verify();
        assertEquals(resultsFor_30306_fromApi,
                cacheManager.getCache("BikeIncidentsByZip").get(ZIP_CODE_30306).get());
        assertEquals(resultsFor_30306_fromApi, resultsFor_30306_fromCache);
    }
}