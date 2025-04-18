package com.bank.userservice.aspect.idempotent;

import com.bank.userservice.service.idempotency.IdempotencyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
@RequiredArgsConstructor
public class IdempotencyAspect {
    private final IdempotencyService idempotencyService;

    @Around("@annotation(idempotent)")
    public Object around(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        HttpServletRequest request = attrs.getRequest();

        String key = switch (idempotent.source()) {
            case HEADER -> request.getHeader(IdempotencyService.IDEMPOTENCY_KEY_HEADER);
            case PARAM -> request.getParameter(IdempotencyService.IDEMPOTENCY_KEY_PARAM);
        };

        if (key == null || key.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing X-idempotency-key");
        }

        return idempotencyService.execute(key, () -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable throwable) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing request", throwable);
            }
        });
    }
}
