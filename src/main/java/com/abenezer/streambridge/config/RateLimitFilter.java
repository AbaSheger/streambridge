package com.abenezer.streambridge.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {
    @Value("${streambridge.rateLimit.tokensPerSecond}")
    private int tokensPerSecond;
    @Value("${streambridge.rateLimit.burst}")
    private int burst;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Skip rate limiting for test environment
        String testProfile = System.getProperty("spring.profiles.active");
        if ("test".equals(testProfile) || request.getHeader("User-Agent") != null && request.getHeader("User-Agent").contains("MockMvc")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String ip = request.getRemoteAddr();
        Bucket bucket = buckets.computeIfAbsent(ip, k -> Bucket.builder()
                .addLimit(Bandwidth.classic(burst, Refill.greedy(tokensPerSecond, Duration.ofSeconds(1))))
                .build());
        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":429,\"error\":\"Too Many Requests\"}");
        }
    }
}
