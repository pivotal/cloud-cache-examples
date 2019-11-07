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
