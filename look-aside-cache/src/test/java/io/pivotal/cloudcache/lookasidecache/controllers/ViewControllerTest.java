/*Copyright (C) 2019-Present Pivotal Software, Inc. All rights reserved.

This program and the accompanying materials are made available under the terms of the under the Apache License, Version
2.0 (the "License”); you may not use this file except in compliance with the License. You may obtain a copy of the
License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
language governing permissions and limitations under the License.*/

package io.pivotal.cloudcache.lookasidecache.controllers;

import io.pivotal.cloudcache.lookasidecache.domain.BikeIncident;
import io.pivotal.cloudcache.lookasidecache.services.BikeIncidentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ViewController.class)
public class ViewControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    BikeIncidentService mockBikeIncidentService;

    @Test
    public void homepage_ShouldLoadSuccessfully() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    public void homepage_ShouldAcceptSearchData() throws Exception {
        ArgumentCaptor<String> zipCodeCaptor = ArgumentCaptor.forClass(String.class);
        String ZIP_CODE = "43567";

        mockMvc.perform(post("/")
                .param("zipCode", ZIP_CODE))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));

        verify(mockBikeIncidentService).getBikeIncidents(zipCodeCaptor.capture());
        assertEquals((ZIP_CODE), zipCodeCaptor.getValue());
    }


    @Test
    @DirtiesContext
    public void homepage_ShouldReturnBikeIncidentsList_GivenValidZipCode() throws Exception {
        String DESCRIPTION = "BIKES!";
        BikeIncident testBikeIncident = new BikeIncident();
        testBikeIncident.setDescription(DESCRIPTION);
        when(mockBikeIncidentService.getBikeIncidents(anyString()))
                .thenReturn(Collections.singletonList(testBikeIncident));

        MvcResult mvcResult = mockMvc.perform(post("/")
                .param("zipCode", "000000"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();
        List<BikeIncident> returnedIncidents =
                (List<BikeIncident>) modelAndView.getModel().get("bikeIncidents");
        assertEquals(DESCRIPTION, returnedIncidents.get(0).getDescription());
    }

    @Test
    public void homepage_ShouldDisplayTimeElapsedForBikeIncidentLookUp() throws Exception {
        BikeIncident testBikeIncident = new BikeIncident();
        when(mockBikeIncidentService.getBikeIncidents(anyString()))
                .thenReturn(Collections.singletonList(testBikeIncident));

        MvcResult mvcResult = mockMvc.perform(post("/")
                .param("zipCode", "000000"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andReturn();

        ModelAndView modelAndView = mvcResult.getModelAndView();
        List<String> lookUpTimes = (List<String>) modelAndView.getModel().get("responseTimes");

        assertEquals(1, lookUpTimes.size());
    }
}
