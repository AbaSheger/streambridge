package com.abenezer.streambridge.controller;

import com.abenezer.streambridge.metrics.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {
    @Autowired
    private MetricsService metricsService;

    @GetMapping
    public Map<String, Object> getMetrics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRequests", metricsService.getTotalRequests());
        stats.put("cacheHits", metricsService.getCacheHits());
        stats.put("cacheMisses", metricsService.getCacheMisses());
        stats.put("bytesSent", metricsService.getBytesSent());
        stats.put("activeStreams", metricsService.getActiveStreams());
        return stats;
    }
}
