package ru.patterns.credit.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.patterns.credit.infrastructure.messaging.sender.KafkaLogSender;
import ru.patterns.credit.infrastructure.metric.RandomErrorMetric;
import ru.patterns.credit.shared.kafka.TraceDto;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Random;

@Aspect
@Component
@RequiredArgsConstructor
public class RandomErrorAspect {
    private final RandomErrorMetric randomErrorMetric;
    private static final Logger logger = LoggerFactory.getLogger(RandomErrorAspect.class);
    private final KafkaLogSender kafkaLogSender;

    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object aroundRestControllerMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        if (shouldReturnError()) {
            log("Вас победил рандом", joinPoint.getSignature().getName());
            randomErrorMetric.incrementError();
            return handleErrorCase(joinPoint);
        } else {
            randomErrorMetric.incrementSuccess();
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

    private void log(String message, String methodName) {
        logger.error(message);

        var traceDto = TraceDto.builder()
                .requestId(MDC.get("requestId"))
                .endpoint(methodName)
                .build();

        kafkaLogSender.send(traceDto);
    }
}
