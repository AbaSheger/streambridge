package com.abenezer.streambridge;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@org.springframework.test.context.ActiveProfiles("test")
public class StreamControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void test200OK() throws Exception {
        String path = "media/sample.mp4";
        Files.write(Paths.get(path), new byte[1024]);
        mockMvc.perform(get("/api/stream/sample.mp4"))
                .andExpect(status().isOk());
        new File(path).delete();
    }

    @Test
    void test206PartialContent() throws Exception {
        String path = "media/sample.mp4";
        Files.write(Paths.get(path), new byte[2048]);
        mockMvc.perform(get("/api/stream/sample.mp4").header("Range", "bytes=0-1023"))
                .andExpect(status().isPartialContent());
        new File(path).delete();
    }

    @Test
    void test404NotFound() throws Exception {
        mockMvc.perform(get("/api/stream/notfound.mp4"))
                .andExpect(status().isNotFound());
    }

    @Test
    void test416InvalidRange() throws Exception {
        String path = "media/sample.mp4";
        Files.write(Paths.get(path), new byte[1024]);
        mockMvc.perform(get("/api/stream/sample.mp4").header("Range", "bytes=9999-10000"))
                .andExpect(status().isRequestedRangeNotSatisfiable());
        new File(path).delete();
    }
}
