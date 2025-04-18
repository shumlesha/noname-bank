package com.bank.notificationservice.aspect;

import com.bank.notificationservice.dto.kafka.TraceDto;
import com.bank.notificationservice.service.kafka.sender.KafkaLogSender;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ExecutionTimeLoggingAspect {
    private final KafkaLogSender kafkaLogSender;

    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object log(ProceedingJoinPoint pjp) throws Throwable{
        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        long duration = System.currentTimeMillis() - start;

        TraceDto dto = TraceDto.builder()
                .requestId(MDC.get("requestId"))
                .endpoint(pjp.getSignature().toShortString())
                .responseTimeMillis(duration)
                .build();

        kafkaLogSender.send(dto);
        return result;
    }
}
