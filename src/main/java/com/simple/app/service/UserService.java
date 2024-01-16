package com.simple.app.service;

import com.simple.app.exception.ErrorCode;
import com.simple.app.exception.SimpleSnsApplicationException;
import com.simple.app.model.Alarm;
import com.simple.app.model.User;
import com.simple.app.model.entity.UserEntity;
import com.simple.app.repository.AlarmEntityRepository;
import com.simple.app.repository.UserEntityRepository;
import com.simple.app.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserEntityRepository userEntityRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AlarmEntityRepository alarmEntityRepository;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token.expired-time-ms}")
    private long expiredTimeMs;

    public User loadUserByUserName(String userName) {
        return userEntityRepository.findByUserName(userName).map(User::fromEntity).orElseThrow(() ->
                new SimpleSnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));
    }

    @Transactional
    public User join(String userName, String password) {
        // 가입된 사용자가 존재하는지
        userEntityRepository.findByUserName(userName).ifPresent(it -> {
            throw new SimpleSnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s is duplicated", userName));
        });

        // 회원가입 진행 > 사용자 등록
        UserEntity userEntity = userEntityRepository.save(UserEntity.of(userName, bCryptPasswordEncoder.encode(password)));

        return User.fromEntity(userEntity);
    }

    public String login(String userName, String password) {
        // 회원가입 여부 체크
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() ->
                new SimpleSnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));

        // 비밀번호 체크
        if (!bCryptPasswordEncoder.matches(password, userEntity.getPassword())) {
            throw new SimpleSnsApplicationException(ErrorCode.INVALID_PASSWORD);
        }

        // 토큰 생성
        String token = JwtTokenUtils.generateToken(userName, secretKey, expiredTimeMs);

        return token;
    }

//    public Page<Alarm> alarmList(String userName, Pageable pageable) {
//        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() ->
//                new SimpleSnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));
//
//        return alarmEntityRepository.findAllByUser(userEntity, pageable).map(Alarm::fromEntity);
//    }

    public Page<Alarm> alarmList(Integer userId, Pageable pageable) {
        return alarmEntityRepository.findAllByUserId(userId, pageable).map(Alarm::fromEntity);
    }
}
