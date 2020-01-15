/*
 * Copyright (C) 2019-Present Pivotal Software, Inc. All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the under the Apache License, Version
 * 2.0 (the "License”); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package sessionstate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.session.data.gemfire.AbstractGemFireOperationsSessionRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;
import java.util.Base64;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SessionStateApplication.class)
@AutoConfigureMockMvc
public class SessionControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    CacheManager cacheManager;

    static String NOTE1 = "Nothing More Than Memories";
    static String NOTE2 = "Macarthur Park";

    @Test
    @DirtiesContext
    public void addSessionNote_should_addNoteToSessionInCache() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/addSessionNote")
                .content(NOTE1))
                .andExpect(status().isOk())
                .andReturn();

        String encodedSessionUUID = mvcResult.getResponse().getCookie("SESSION").getValue();

        List<String> notesList = getNotesForSessionInCache(encodedSessionUUID);

        assertEquals(NOTE1, (notesList.get(0)));
    }

    @Test
    @DirtiesContext
    public void addSessionNote_should_addMultipleNotesToSessionInCache() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/addSessionNote")
                .content(NOTE1))
                .andExpect(status().isOk())
                .andReturn();

        String encodedSessionUUID = mvcResult.getResponse().getCookie("SESSION").getValue();

        mockMvc.perform(post("/addSessionNote")
                .content(NOTE2)
                .cookie(new Cookie("SESSION", encodedSessionUUID)))
                .andExpect(status().isOk());

        List<String> notesList = getNotesForSessionInCache(encodedSessionUUID);

        assertEquals(2, notesList.size());
        assertEquals(NOTE1, (notesList.get(0)));
        assertEquals(NOTE2, (notesList.get(1)));
    }

    @Test
    @DirtiesContext
    public void getSessionNotes_should_returnAllNotesForSessionInCache() throws Exception {
        MvcResult mvcPostResult = mockMvc.perform(post("/addSessionNote")
                .content(NOTE1))
                .andExpect(status().isOk())
                .andReturn();

        String encodedSessionUUID = mvcPostResult.getResponse().getCookie("SESSION").getValue();

        mockMvc.perform(get("/getSessionNotes")
                .cookie(new Cookie("SESSION", encodedSessionUUID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0]").value(NOTE1));
    }

    @Test
    @DirtiesContext
    public void invalidateSession_should_removeSessionFromCache() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/addSessionNote")
                .content("Just to create session!"))
                .andExpect(status().isOk())
                .andReturn();

        String encodedSessionUUID = mvcResult.getResponse().getCookie("SESSION").getValue();

        mockMvc.perform(post("/invalidateSession")
                .cookie(new Cookie("SESSION", encodedSessionUUID)))
                .andExpect(status().isOk());

        ValueWrapper sessionContents = getCachedSessionUUIDs(encodedSessionUUID);

        assertNull(sessionContents);
    }

    private List<String> getNotesForSessionInCache(String encodedSessionUUID) {

        String sessionUUID = decodeBase64(encodedSessionUUID);
        List<String> notesFromCache = getNotesForSession(sessionUUID);

        return notesFromCache;
    }

    private ValueWrapper getCachedSessionUUIDs(String encodedSessionUUID) {
        String sessionUUID = decodeBase64(encodedSessionUUID);

        return cacheManager
                .getCache("ClusteredSpringSessions")
                .get(sessionUUID);
    }

    private String decodeBase64(String base64EncodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64EncodedString);
        return new String(decodedBytes);
    }

    private List<String> getNotesForSession(String sessionUUID) {
        return (List<String>) ((AbstractGemFireOperationsSessionRepository.GemFireSession)
                cacheManager
                        .getCache("ClusteredSpringSessions")
                        .get(sessionUUID)
                        .get())
                .getAttribute("NOTES");
    }
}
