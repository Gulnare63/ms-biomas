package com.example.audit.aspect;

import com.example.audit.aop.annotation.AuditLog;
import com.example.audit.dao.entity.AuditLogEntity;
import com.example.audit.dao.repository.AuditLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final ObjectMapper objectMapper;
    private final AuditLogRepository repo;

    @Pointcut("within(@com.example.audit.aop.annotation.AuditLog *) && !@annotation(com.example.audit.aop.annotation.AuditIgnore)")
    public void auditPointcut() {}

    @Around("auditPointcut() && @within(auditLog)")
    public Object around(ProceedingJoinPoint joinPoint, AuditLog auditLog) throws Throwable {
        long start = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String handler = signature.getDeclaringType().getSimpleName() + "." + signature.getName();

        HttpServletRequest req = currentRequest();
        String action = req != null ? req.getMethod() : "N/A";
        String path = req != null ? req.getRequestURI() : "N/A";
        String ip = req != null ? getIp(req) : null;

        String username = currentUsername();

        // ✅ Login zamanı SecurityContext boş ola bilər -> username-i requestdən götür
        username = enrichUsernameForAuthEndpoints(username, path, joinPoint.getArgs());

        // ✅ args həmişə yazılsın, sadəcə sensitive field-lər mask olsun
        String argsJson = safeArgs(joinPoint.getArgs());

        Object response = null;
        Integer status = 200;
        String error = null;

        try {
            response = joinPoint.proceed();

            if (response instanceof ResponseEntity<?> re) {
                status = re.getStatusCode().value();
            }
            return response;

        } catch (Throwable t) {
            status = 500;
            error = t.getMessage();
            throw t;

        } finally {
            long duration = System.currentTimeMillis() - start;

            try {
                AuditLogEntity entity = AuditLogEntity.builder()
                        .username(username)
                        .module(auditLog.module())
                        .action(action)
                        .path(path)
                        .handler(handler)
                        .arguments(argsJson)
                        .status(status)
                        .durationMs(duration)
                        .errorMessage(error)
                        .ip(ip)
                        .build();

                repo.save(entity);
            } catch (Exception e) {
                log.warn("AUDIT_SAVE_FAILED handler={} msg={}", handler, e.getMessage());
            }
        }
    }

    private HttpServletRequest currentRequest() {
        try {
            return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        } catch (Exception e) {
            return null;
        }
    }

    private String currentUsername() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return "Anonymous";
        }
        return auth.getName();
    }

    private String enrichUsernameForAuthEndpoints(String currentUsername, String path, Object[] args) {
        if (path == null) return currentUsername;

        // Login zamanı username request body-də olur
        if ("Anonymous".equals(currentUsername) && path.startsWith("/auth/login")) {
            String u = extractFieldAsString(args, "username");
            if (u != null && !u.isBlank()) return u;
        }

        // (opsional) refresh/logout üçün də istəsən ayrıca logic qura bilərik
        return currentUsername;
    }

    private String extractFieldAsString(Object[] args, String fieldName) {
        if (args == null || args.length == 0 || args[0] == null) return null;
        Object first = args[0];
        try {
            var f = first.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            Object val = f.get(first);
            return val == null ? null : val.toString();
        } catch (Exception ignored) {
            return null;
        }
    }

    private String getIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        return (xff != null && !xff.isBlank()) ? xff : request.getRemoteAddr();
    }

    private String safeArgs(Object[] args) {
        String json;
        try {
            json = objectMapper.writeValueAsString(args);
        } catch (Exception e) {
            json = java.util.Arrays.toString(args);
        }
        json = redactCredentials(json);
        return truncate(json);
    }

    private String truncate(String s) {
        if (s == null) return null;
        return s.length() > 2000 ? s.substring(0, 2000) + "...(truncated)" : s;
    }

    // ✅ Username görünsün, password/token mask olsun
    private String redactCredentials(String input) {
        if (input == null) return null;

        // username burdan çıxarıldı!
        String[] sensitive = {"password", "accessToken", "refreshToken", "token"};

        for (String field : sensitive) {
            String regex = String.format("\"%s\"\\s*:\\s*\"(.*?)\"", field);
            input = input.replaceAll(regex, "\"" + field + "\":\"********\"");
        }
        return input;
    }
}
