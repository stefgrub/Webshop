package com.example.shop.web;

import com.example.shop.domain.AuditLog;
import com.example.shop.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;

public class AuditWriteInterceptor implements HandlerInterceptor {
    private final AuditLogService audit;

    public AuditWriteInterceptor(AuditLogService audit) { this.audit = audit; }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        final String path = req.getRequestURI();
        final String method = req.getMethod();

        if (!path.startsWith("/admin/") || !isWrite(method) || path.startsWith("/admin/logs")) {
            return true;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null) ? auth.getName() : "anonymous";

        Map<String,Object> form = formParams(req);
        var log = new AuditLog();
        log.setAdminUsername(username);
        log.setAdminIp(clientIp(req));
        log.setUserAgent(req.getHeader("User-Agent"));
        log.setAction(guessAction(method, path));
        log.setEntityType(guessEntity(path));
        log.setEntityId(guessId(path));
        log.setPath(path);
        log.setRequestMethod(method);
        log.setQueryString(req.getQueryString());
        log.setRequestBodyMasked(audit.mask(form));

        audit.save(log);
        return true;
    }

    private boolean isWrite(String m) { return "POST".equals(m)||"PUT".equals(m)||"PATCH".equals(m)||"DELETE".equals(m); }

    private String clientIp(HttpServletRequest req){
        String xf = req.getHeader("X-Forwarded-For");
        return (xf!=null && !xf.isBlank()) ? xf.split(",")[0].trim() : req.getRemoteAddr();
    }

    private String guessAction(String method, String path) {
        return switch (method) {
            case "POST" -> path.endsWith("/delete") ? "delete" : "create";
            case "PUT","PATCH" -> "update";
            case "DELETE" -> "delete";
            default -> "unknown";
        };
    }

    private String guessEntity(String path) {
        // /admin/products/123/edit -> products
        String[] p = path.split("/");
        return p.length>2 ? p[2] : null;
    }

    private String guessId(String path) {
        for (String p : path.split("/")) if (p.matches("\\d+")) return p;
        return null;
    }

    private Map<String,Object> formParams(HttpServletRequest req) {
        try {
            if (req.getContentType()!=null &&
                    req.getContentType().toLowerCase().startsWith("application/x-www-form-urlencoded")) {
                Map<String,String[]> pm = req.getParameterMap();
                Map<String,Object> flat = new HashMap<>();
                pm.forEach((k,v) -> flat.put(k, (v!=null && v.length==1) ? v[0] : v));
                return flat;
            }
        } catch (Exception ignored) {}
        return null;
    }
}