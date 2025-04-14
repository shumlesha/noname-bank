package com.bank.userservice.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
public class RandomErrorAspect {

    private static final Logger log = LoggerFactory.getLogger(RandomErrorAspect.class);

    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object aroundRestControllerMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        if (shouldReturnError()) {
            log.error("Вас победил рандом (Random error triggered)");
            return handleErrorCase(joinPoint);
        } else {
            return joinPoint.proceed();
        }
    }


    private boolean shouldReturnError() {
        int currentMinute = LocalDateTime.now().getMinute();
        double errorProbability = (currentMinute % 2 == 0) ? 0.9 : 0.5;
        return Math.random() < errorProbability;
    }


    private Object handleErrorCase(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Class<?> returnType = method.getReturnType();


        if (ResponseEntity.class.isAssignableFrom(returnType)) {
            return ResponseEntity.internalServerError().body("Ошибка (Simulated Error)");
        } else {
            throw new RuntimeException("Ошибка (Simulated Error)");
        }
    }
}