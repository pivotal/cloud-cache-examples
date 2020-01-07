package sessionstate;

import org.apache.geode.internal.cache.LocalRegion;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.session.data.gemfire.AbstractGemFireOperationsSessionRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        mockMvc.perform(post("/addSessionNote")
                .content(NOTE1))
                .andExpect(status().isOk());

        List<String> notesList = getNotesForLastSessionInCache();

        assertEquals(NOTE1, (notesList.get(0)));
    }

    @Test
    @DirtiesContext
    public void addSessionNote_should_addMultipleNotesToSessionInCache() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/addSessionNote")
                .content(NOTE1))
                .andExpect(status().isOk())
                .andReturn();

        String sessionCookie = mvcResult.getResponse().getCookie("SESSION").getValue();

        mockMvc.perform(post("/addSessionNote")
                .content(NOTE2)
                .cookie(new Cookie("SESSION", sessionCookie)))
                .andExpect(status().isOk());

        List<String> notesList = getNotesForLastSessionInCache();

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

        String sessionCookie = mvcPostResult.getResponse().getCookie("SESSION").getValue();

        mockMvc.perform(get("/getSessionNotes")
                .cookie(new Cookie("SESSION", sessionCookie)))
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

        String sessionCookie = mvcResult.getResponse().getCookie("SESSION").getValue();

        mockMvc.perform(post("/invalidateSession")
                .cookie(new Cookie("SESSION", sessionCookie)))
                .andExpect(status().isOk());

        Iterator<String> keys = getCachedSessionUUIDs();

        assertFalse(keys.hasNext());
    }

    private List<String> getNotesForLastSessionInCache() {

        Iterator<String> sessionUUIDs = getCachedSessionUUIDs();
        String sessionUUID = getUUIDofLastSession(sessionUUIDs);
        List<String> notesFromCache = getNotesForSession(sessionUUID);

        return notesFromCache;
    }

    private List<String> getNotesForSession(String sessionUUID) {
        return (List<String>) ((AbstractGemFireOperationsSessionRepository.GemFireSession)
                cacheManager
                        .getCache("ClusteredSpringSessions")
                        .get(sessionUUID)
                        .get())
                .getAttribute("NOTES");
    }

    private Iterator<String> getCachedSessionUUIDs() {
        return (Iterator<String>) ((LocalRegion) cacheManager
                .getCache("ClusteredSpringSessions")
                .getNativeCache())
                .keys()
                .iterator();
    }

    private String getUUIDofLastSession(Iterator<String> sessionUUIDs) {

        String UUID = "";

        while (sessionUUIDs.hasNext()) {
            UUID = sessionUUIDs.next();
        }

        return UUID;
    }
}
