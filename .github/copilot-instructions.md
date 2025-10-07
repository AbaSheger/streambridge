## StreamBridge Project - Local CDN Video Streaming Backend

This is a Spring Boot 3 application that simulates a local CDN-style video streaming backend with the following features:

### Core Features
- **Streaming API**: HTTP Range Request support for video/audio files from `/media` folder
- **Caching**: Caffeine-based caching for file metadata (size, MIME type, modified date)
- **Rate Limiting**: Bucket4j rate limiter per IP address (configurable tokens/burst)
- **Metrics**: Thread-safe counters for requests, cache hits/misses, bytes sent, active streams
- **Error Handling**: Global JSON error responses with proper HTTP status codes
- **Optional Authentication**: Bearer token protection for endpoints when configured

### Endpoints
- `GET /api/stream/{filename}` - Stream files with HTTP Range support (200/206 responses)
- `GET /api/metrics` - System metrics in JSON format
- `GET /actuator/health` - Spring Boot health check
- `GET /` - Demo page for testing video playback

### Quick Start
```bash
mvn spring-boot:run
# or use VS Code task "Run StreamBridge"
# Access demo at http://localhost:8080
# Place sample.mp4 in /media folder for testing
```

### Configuration
See `src/main/resources/application.yml` for:
- Media folder path
- Rate limiting settings (tokens per second, burst capacity)
- Optional bearer token authentication
- Cache configuration

### Architecture
The application demonstrates CDN-style patterns: streaming with partial content support, intelligent caching, rate limiting, and comprehensive metrics collection - all essential for scalable media delivery systems.
