package com.bank.DigitalBank.Filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@Order(2)
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if(!path.contains("/transaction")){
            filterChain.doFilter(request,response);
            return ;
        }
        String key;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            key = authentication.getName(); // From JWT
        } else {
            key = request.getRemoteAddr(); // Fallback to IP address
        }

        Bucket bucket= buckets.computeIfAbsent(key,this::newBucket);
        if(bucket.tryConsume(1)){
            filterChain.doFilter(request,response);
        }
        else{
            response.setStatus(429); // Too Many Requests
            response.getWriter().write("🚫 Rate limit exceeded. Try again later.");
        }

    }

    private Bucket newBucket(String string) {
        Refill refill = Refill.intervally(10, Duration.ofHours(1));
        Bandwidth limit = Bandwidth.classic(10,refill);
        return Bucket.builder().addLimit(limit).build();
    }
}
