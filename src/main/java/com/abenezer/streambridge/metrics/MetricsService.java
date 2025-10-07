package com.abenezer.streambridge.metrics;

import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MetricsService {
    private final AtomicLong totalRequests = new AtomicLong();
    private final AtomicLong cacheHits = new AtomicLong();
    private final AtomicLong cacheMisses = new AtomicLong();
    private final AtomicLong bytesSent = new AtomicLong();
    private final AtomicLong activeStreams = new AtomicLong();

    public void incrementTotalRequests() { totalRequests.incrementAndGet(); }
    public void incrementCacheHits() { cacheHits.incrementAndGet(); }
    public void incrementCacheMisses() { cacheMisses.incrementAndGet(); }
    public void addBytesSent(long bytes) { bytesSent.addAndGet(bytes); }
    public void incrementActiveStreams() { activeStreams.incrementAndGet(); }
    public void decrementActiveStreams() { activeStreams.decrementAndGet(); }

    public long getTotalRequests() { return totalRequests.get(); }
    public long getCacheHits() { return cacheHits.get(); }
    public long getCacheMisses() { return cacheMisses.get(); }
    public long getBytesSent() { return bytesSent.get(); }
    public long getActiveStreams() { return activeStreams.get(); }
}
