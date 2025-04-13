package ru.patterns.credit.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Random;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class RandomErrorAspect {

    private final Random random = new Random();
    private final RandomErrorProperties properties;

    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object aroundRestControllerMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        if (properties.isEnabled() && shouldReturnError()) {
            log.error("Вас победил рандом");
            return handleErrorCase(joinPoint);
        } else {
            return joinPoint.proceed();
        }
    }

    private boolean shouldReturnError() {
        int currentMinute = LocalDateTime.now().getMinute();
        double errorProbability = (currentMinute % 2 == 0) ? 0.9 : 0.5;
        return random.nextDouble() < errorProbability;
    }

    private Object handleErrorCase(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Class<?> returnType = method.getReturnType();

        if (ResponseEntity.class.isAssignableFrom(returnType)) {
            return ResponseEntity.internalServerError().body("Ошибка рандома");
        } else {
            throw new RuntimeException("Ошибка рандома");
        }
    }
}
