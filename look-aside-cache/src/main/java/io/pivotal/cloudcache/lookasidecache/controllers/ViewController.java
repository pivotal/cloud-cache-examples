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

package io.pivotal.cloudcache.lookasidecache.controllers;

import io.pivotal.cloudcache.lookasidecache.domain.BikeIncident;
import io.pivotal.cloudcache.lookasidecache.services.BikeIncidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ViewController {
    @Autowired
    BikeIncidentService bikeIncidentService;

    private List<String> responseTimes = new ArrayList<>();

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("zipCode");
        return "home";
    }

    @PostMapping("/")
    public String requestIncidents(
            @RequestParam String zipCode,
            Model model) throws IOException {

        long timeStampBeforeQuery = System.currentTimeMillis();
        List<BikeIncident> bikeIncidents =
                bikeIncidentService.getBikeIncidents(zipCode);
        long timeElapsed = System.currentTimeMillis() - timeStampBeforeQuery;

        recordNewDataRequest(timeElapsed, zipCode);
        populateModelWithSearchResults(model, bikeIncidents, zipCode);

        return "home";
    }

    private void recordNewDataRequest(long timeElapsed, String zipCode) {
        responseTimes.add( "Zip Code: " + zipCode + ", Response Time: " + timeElapsed + " ms");
    }

    private void populateModelWithSearchResults(Model model, List<BikeIncident> bikeIncidents, String zipCode) {
        model.addAttribute("zipCode", zipCode);
        model.addAttribute("responseTimes", responseTimes);
        model.addAttribute("bikeIncidents", bikeIncidents);
    }
}
