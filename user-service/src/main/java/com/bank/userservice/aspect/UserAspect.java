package com.bank.userservice.aspect;

import com.bank.userservice.dto.event.EventMessage;
import com.bank.userservice.dto.event.payload.UserBanPayload;
import com.bank.userservice.dto.user.BanUserRequest;
import com.bank.userservice.dto.user.UserDto;
import com.bank.userservice.kafka.sender.SenderService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;


@Aspect
@Component
@RequiredArgsConstructor
public class UserAspect {
    private static final String USER_BAN_EVENT_TYPE = "USER_BAN";

    private final SenderService<EventMessage<?>> senderService;

    @AfterReturning(pointcut = "@annotation(publishBanEvent)", returning = "result")
    public void publishEvent(JoinPoint joinPoint, Object result, PublishBanEvent publishBanEvent) {
        EventMessage<?> eventMessage = buildEvent(joinPoint, result);

        if (eventMessage == null) {
            return;
        }

        for (String topic : publishBanEvent.topics()) {
            senderService.sendEvent(topic, eventMessage);
        }
    }

    private EventMessage<?> buildEvent(JoinPoint joinPoint, Object result) {
        if (result instanceof UserDto user) {
            Object[] args = joinPoint.getArgs();
            UUID currentUserId = (UUID) args[2];
            BanUserRequest banUserRequest = (BanUserRequest) args[1];

            UserBanPayload payload = new UserBanPayload(user.getId(), currentUserId, banUserRequest.getReason());
            return new EventMessage<>(USER_BAN_EVENT_TYPE, Instant.now(), payload);
        }
        return null;
    }
}
