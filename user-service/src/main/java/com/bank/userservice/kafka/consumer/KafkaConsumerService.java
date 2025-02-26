package com.bank.userservice.kafka.consumer;

import com.bank.userservice.dispatch.EventDispatcher;
import com.bank.userservice.dto.event.EventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final EventDispatcher eventDispatcher;

    @KafkaListener(
            topics = "#{@topicsConfig.getByType(T(com.bank.userservice.kafka.config.TopicsConfig.TopicType).CONSUME)}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeEvent(EventMessage<?> eventMessage, Acknowledgment acknowledgment) {
        try {
            eventDispatcher.dispatch(eventMessage);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error while processing event: {}", eventMessage, e);
        }
    }
}
