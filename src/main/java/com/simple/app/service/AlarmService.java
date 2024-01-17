package com.simple.app.service;

import com.simple.app.exception.ErrorCode;
import com.simple.app.exception.SimpleSnsApplicationException;
import com.simple.app.model.AlarmArgs;
import com.simple.app.model.AlarmType;
import com.simple.app.model.entity.AlarmEntity;
import com.simple.app.model.entity.UserEntity;
import com.simple.app.repository.AlarmEntityRepository;
import com.simple.app.repository.EmitterRepository;
import com.simple.app.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmService {
    private final static Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final static String ALARM_NAME = "alarm";

    private final EmitterRepository emitterRepository;
    private final AlarmEntityRepository alarmEntityRepository;
    private final UserEntityRepository userEntityRepository;

    public void send(AlarmType alarmType, AlarmArgs alarmArgs, Integer receiveUserId) {
        UserEntity userEntity = userEntityRepository.findById(receiveUserId).orElseThrow(() -> new SimpleSnsApplicationException(ErrorCode.USER_NOT_FOUND));

        // alarm save
        AlarmEntity alarmEntity = alarmEntityRepository.save(AlarmEntity.of(userEntity, alarmType, alarmArgs));

        emitterRepository.get(receiveUserId).ifPresentOrElse(sseEmitter -> {
            try {
                sseEmitter.send(SseEmitter.event().id(alarmEntity.getId().toString()).name(ALARM_NAME).data("New alarm"));
            } catch (IOException e) {
                emitterRepository.delete(receiveUserId);
                throw new SimpleSnsApplicationException(ErrorCode.ALARM_CONNECT_ERROR);
            }
        }, () -> log.info("No emitter founded"));
    }

    public SseEmitter connectAlarm(Integer userId) {
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);

        emitterRepository.set(userId, sseEmitter);

        sseEmitter.onCompletion(() -> emitterRepository.delete(userId));
        sseEmitter.onTimeout(() -> emitterRepository.delete(userId));

        try {
            sseEmitter.send(SseEmitter.event().id("").name(ALARM_NAME).comment("Connect completed"));
        } catch (IOException e) {
            throw new SimpleSnsApplicationException(ErrorCode.ALARM_CONNECT_ERROR);
        }

        return sseEmitter;
    }
}
