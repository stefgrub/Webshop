package com.example.shop.web;

import com.example.shop.domain.AuditLog;
import com.example.shop.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class AuditWriteInterceptor implements HandlerInterceptor {

    private final AuditLogService audit;

    public AuditWriteInterceptor(AuditLogService audit) {
        this.audit = audit;
    }

    private boolean isWriteMethod(String m) {
        return "POST".equals(m) || "PUT".equals(m) || "PATCH".equals(m) || "DELETE".equals(m);
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res, Object handler, Exception ex) {
        final String path = req.getRequestURI();
        final String method = req.getMethod();
        if (!path.startsWith("/admin")) return;     // nur Admin-Bereich
        if (!isWriteMethod(method)) return;         // nur schreibende Requests

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null && auth.isAuthenticated()) ? auth.getName() : "anonymous";

        // Request-Body ermitteln (wenn ContentCachingRequestFilter aktiv ist)
        Map<String, Object> body = readBodyOrParams(req);

        AuditLog log = new AuditLog();
        log.setAdmin(username);
        log.setAdminIp(getClientIp(req));
        log.setUserAgent(Optional.ofNullable(req.getHeader("User-Agent")).orElse(""));

        // optional: von Controllern/Services später konkret setzen
        log.setEntity(null);
        log.setEntityId(null);
        log.setAction(method);

        log.setPath(path);
        log.setRequestMethod(method);
        log.setQueryString(req.getQueryString());

        log.setRequestBodyMasked(audit.mask(body));
        log.setDiffBeforeJson(null);
        log.setDiffAfterJson(null);

        audit.save(log);
    }

    /** liest JSON/urlencoded, fällt sonst auf Parameter zurück */
    private Map<String, Object> readBodyOrParams(HttpServletRequest req) {
        // Versuche ContentCachingRequestWrapper
        if (req instanceof ContentCachingRequestWrapper w) {
            byte[] buf = w.getContentAsByteArray();
            if (buf != null && buf.length > 0) {
                String raw = new String(buf, StandardCharsets.UTF_8);
                Map<String, Object> asMap = tryParseFormOrJson(raw);
                if (asMap != null && !asMap.isEmpty()) return asMap;
            }
        }
        // Fallback: Parameter-Map (GET/POST form)
        Map<String, Object> m = new LinkedHashMap<>();
        req.getParameterMap().forEach((k, v) -> m.put(k, (v != null && v.length == 1) ? v[0] : v));
        return m;
    }

    private String getClientIp(HttpServletRequest req) {
        String xf = req.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) return xf.split(",")[0].trim();
        return req.getRemoteAddr();
    }

    /** sehr einfache Heuristik: JSON erkennen, sonst urlencoded versuchen */
    private Map<String, Object> tryParseFormOrJson(String s) {
        if (s == null || s.isBlank()) return null;
        String t = s.trim();

        // JSON? (hier nur als raw speichern; echte Parse kannst du mit Jackson nachrüsten)
        if ((t.startsWith("{") && t.endsWith("}")) || (t.startsWith("[") && t.endsWith("]"))) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("_raw", t);
            return m;
        }

        // urlencoded key=value&key2=value2
        Map<String, Object> form = new LinkedHashMap<>();
        String[] pairs = t.split("&");
        for (String pair : pairs) {
            if (pair.isEmpty()) continue;
            int i = pair.indexOf('=');
            if (i > -1) {
                String k = decode(pair.substring(0, i));
                String v = decode(pair.substring(i + 1));
                form.put(k, v);
            } else {
                form.put(decode(pair), "");
            }
        }
        if (form.isEmpty()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("_raw", t);
            return m;
        }
        return form;
    }

    private String decode(String s) {
        try {
            return java.net.URLDecoder.decode(s, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return s;
        }
    }
}