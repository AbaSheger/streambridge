package com.abenezer.streambridge.service;

import com.abenezer.streambridge.util.FileMetadata;
import com.abenezer.streambridge.util.Range;
import com.abenezer.streambridge.metrics.MetricsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

@Service
public class StreamService {
    @Value("${streambridge.mediaRoot}")
    private String mediaRoot;
    @Value("${streambridge.smallFileThresholdBytes}")
    private long smallFileThresholdBytes;

    private final MetricsService metricsService;
    private static final Pattern SAFE_FILENAME = Pattern.compile("^[a-zA-Z0-9._-]+$");

    public StreamService(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @Cacheable("fileMetadata")
    public FileMetadata getFileMetadata(String filename) throws IOException {
        Path file = Paths.get(mediaRoot, filename);
        if (!Files.exists(file)) throw new FileNotFoundException(filename);
        String mime = Files.probeContentType(file);
        if (mime == null) mime = "application/octet-stream";
        long size = Files.size(file);
        long modified = Files.getLastModifiedTime(file).toMillis();
        return new FileMetadata(size, mime, modified);
    }

    public ResponseEntity<?> handleStreamRequest(String filename, String rangeHeader, HttpServletRequest request) throws IOException {
        if (!SAFE_FILENAME.matcher(filename).matches()) {
            return error(HttpStatus.BAD_REQUEST, "Invalid filename: " + filename, request.getRequestURI());
        }
        Path file = Paths.get(mediaRoot, filename);
        if (!Files.exists(file)) {
            return error(HttpStatus.NOT_FOUND, "File not found: " + filename, request.getRequestURI());
        }
        FileMetadata meta = getFileMetadata(filename);
        metricsService.incrementTotalRequests();
        long fileSize = meta.getSize();
        String mime = meta.getMimeType();
        if (rangeHeader == null) {
            metricsService.addBytesSent(fileSize);
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file.toFile()));
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mime))
                    .contentLength(fileSize)
                    .body(resource);
        }
        Range range = Range.parse(rangeHeader, fileSize);
        if (range == null) {
            return error(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE, "Invalid range: " + rangeHeader, request.getRequestURI());
        }
        long start = range.getStart();
        long end = range.getEnd();
        if (start >= fileSize || end >= fileSize || start > end) {
            return error(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE, "Range out of bounds: " + rangeHeader, request.getRequestURI());
        }
        long length = end - start + 1;
        metricsService.addBytesSent(length);
        InputStream inputStream = new FileInputStream(file.toFile());
        inputStream.skip(start);
        InputStreamResource resource = new InputStreamResource(new LimitedInputStream(inputStream, length));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(mime));
        headers.setContentLength(length);
        headers.set("Content-Range", "bytes " + start + "-" + end + "/" + fileSize);
        return new ResponseEntity<>(resource, headers, HttpStatus.PARTIAL_CONTENT);
    }

    private ResponseEntity<?> error(HttpStatus status, String message, String path) {
        return ResponseEntity.status(status).body(new ErrorResponse(status.value(), status.getReasonPhrase(), message, path));
    }

    static class LimitedInputStream extends FilterInputStream {
        private long left;
        protected LimitedInputStream(InputStream in, long left) {
            super(in);
            this.left = left;
        }
        @Override
        public int read() throws IOException {
            if (left <= 0) return -1;
            int b = super.read();
            if (b != -1) left--;
            return b;
        }
        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (left <= 0) return -1;
            len = (int) Math.min(len, left);
            int n = super.read(b, off, len);
            if (n != -1) left -= n;
            return n;
        }
    }

    static class ErrorResponse {
        public final String timestamp = java.time.Instant.now().toString();
        public final int status;
        public final String error;
        public final String message;
        public final String path;
        public ErrorResponse(int status, String error, String message, String path) {
            this.status = status;
            this.error = error;
            this.message = message;
            this.path = path;
        }
    }
}
