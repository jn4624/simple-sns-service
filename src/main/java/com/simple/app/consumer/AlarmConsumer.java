package com.simple.app.consumer;

import com.simple.app.model.event.AlarmEvent;
import com.simple.app.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlarmConsumer {
    private final AlarmService alarmService;

    @KafkaListener(topics = "${spring.kafka.topic.alarm}")
    public void consumeAlarm(AlarmEvent alarmEvent, Acknowledgment acknowledgment) {
        log.info("Consume the event {}", alarmEvent);

        alarmService.send(alarmEvent.getAlarmType(), alarmEvent.getAlarmArgs(), alarmEvent.getReceiveUserId());
        acknowledgment.acknowledge();
    }
}
