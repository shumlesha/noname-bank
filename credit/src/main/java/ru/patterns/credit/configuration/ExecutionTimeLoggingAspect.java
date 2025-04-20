package ru.patterns.credit.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import ru.patterns.credit.infrastructure.messaging.sender.KafkaLogSender;
import ru.patterns.credit.shared.kafka.TraceDto;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ExecutionTimeLoggingAspect {
    private final KafkaLogSender kafkaLogSender;

    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object log(ProceedingJoinPoint pjp) throws Throwable{
        log.info("Дата");
        long start = System.currentTimeMillis();
        Object result = pjp.proceed();
        long duration = System.currentTimeMillis() - start;

        var dto = TraceDto.builder()
                .requestId(MDC.get("requestId"))
                .serviceName("credit")
                .endpoint(pjp.getSignature().toShortString())
                .responseTimeMillis(duration)
                .build();
        log.info(dto.toString());
        kafkaLogSender.send(dto);
        return result;
    }
}
