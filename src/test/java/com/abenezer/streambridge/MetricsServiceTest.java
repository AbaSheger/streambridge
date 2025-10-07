package com.abenezer.streambridge;

import com.abenezer.streambridge.metrics.MetricsService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MetricsServiceTest {
    @Test
    void testCountersIncrement() {
        MetricsService metrics = new MetricsService();
        metrics.incrementTotalRequests();
        metrics.incrementCacheHits();
        metrics.incrementCacheMisses();
        metrics.addBytesSent(100);
        metrics.incrementActiveStreams();
        metrics.decrementActiveStreams();
        assertEquals(1, metrics.getTotalRequests());
        assertEquals(1, metrics.getCacheHits());
        assertEquals(1, metrics.getCacheMisses());
        assertEquals(100, metrics.getBytesSent());
        assertEquals(0, metrics.getActiveStreams());
    }
}
