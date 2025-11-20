package com.example.shop.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RegistrationRateLimitFilter extends OncePerRequestFilter {

    private static final long WINDOW_MILLIS = 5 * 60 * 1000; // 5 Minuten
    private static final int MAX_PER_IP = 20;                // z.B. 20 Registrierungen / 5 Min / IP
    private static final int MAX_GLOBAL = 200;               // z.B. 200 insgesamt / 5 Min

    private static class Counter {
        long windowStart;
        int count;
    }

    private final Map<String, Counter> perIp = new ConcurrentHashMap<>();
    private final Counter global = new Counter();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Nur POST /register begrenzen, alles andere durchlassen
        if (!"/register".equals(path) || !"POST".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        long now = System.currentTimeMillis();
        String ip = getClientIp(request);

        // --- IP-basiertes Fenster ---
        Counter ipCounter = perIp.computeIfAbsent(ip, k -> {
            Counter c = new Counter();
            c.windowStart = now;
            c.count = 0;
            return c;
        });

        synchronized (ipCounter) {
            if (now - ipCounter.windowStart > WINDOW_MILLIS) {
                ipCounter.windowStart = now;
                ipCounter.count = 0;
            }
            ipCounter.count++;
            if (ipCounter.count > MAX_PER_IP) {
                writeRateLimitResponse(response);
                return; // NICHT weiter in die Kette
            }
        }

        // --- Globales Fenster ---
        synchronized (global) {
            if (now - global.windowStart > WINDOW_MILLIS) {
                global.windowStart = now;
                global.count = 0;
            }
            global.count++;
            if (global.count > MAX_GLOBAL) {
                writeRateLimitResponse(response);
                return; // NICHT weiter in die Kette
            }
        }

        // alles ok -> weiter
        filterChain.doFilter(request, response);
    }

    private void writeRateLimitResponse(HttpServletResponse response) throws IOException {
        // Wenn schon was anderes geschrieben hat, nicht noch mal rumpfuschen
        if (response.isCommitted()) {
            return;
        }

        // Buffer leeren, damit nichts von späteren Komponenten drin ist
        response.resetBuffer();
        response.setStatus(429);
        response.setContentType("text/plain;charset=UTF-8");

        try (PrintWriter writer = response.getWriter()) {
            writer.write("Zu viele Registrierungsversuche. Bitte versuche es später erneut.");
            writer.flush();
        }

        // NICHT sendError(), NICHT weiterreichen → kein /error-View
    }

    private String getClientIp(HttpServletRequest request) {
        String hdr = request.getHeader("X-Forwarded-For");
        if (hdr != null && !hdr.isBlank()) {
            return hdr.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}