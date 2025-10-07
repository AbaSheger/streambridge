package com.abenezer.streambridge.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig implements WebMvcConfigurer {
    @Value("${streambridge.security.bearer}")
    private String bearerToken;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (bearerToken != null && !bearerToken.isEmpty()) {
            registry.addInterceptor(new BearerAuthInterceptor(bearerToken))
                    .addPathPatterns("/api/stream/**", "/api/metrics");
        }
    }

    static class BearerAuthInterceptor implements HandlerInterceptor {
        private final String expectedToken;
        BearerAuthInterceptor(String expectedToken) {
            this.expectedToken = expectedToken;
        }
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            String auth = request.getHeader("Authorization");
            if (auth == null || !auth.equals("Bearer " + expectedToken)) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"status\":403,\"error\":\"Forbidden\"}");
                return false;
            }
            return true;
        }
    }
}
