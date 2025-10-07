package com.abenezer.streambridge;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@org.springframework.test.context.ActiveProfiles("test")
public class RateLimitFilterTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testRateLimitBypassedInTestProfile() throws Exception {
        // In test profile, rate limiting should be bypassed
        for (int i = 0; i < 25; i++) {
            mockMvc.perform(get("/api/stream/notfound.mp4"))
                    .andExpect(status().isNotFound()); // Should get 404, not 429
        }
    }
}
