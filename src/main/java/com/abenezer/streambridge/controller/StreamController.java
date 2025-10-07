package com.abenezer.streambridge.controller;

import com.abenezer.streambridge.service.StreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/api/stream")
public class StreamController {
    @Autowired
    private StreamService streamService;

    @GetMapping("/{filename}")
    public ResponseEntity<?> streamFile(
            @PathVariable String filename,
            @RequestHeader(value = "Range", required = false) String range,
            HttpServletRequest request) throws IOException {
        return streamService.handleStreamRequest(filename, range, request);
    }
}
