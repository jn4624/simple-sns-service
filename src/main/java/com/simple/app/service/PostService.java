package com.simple.app.service;

import com.simple.app.exception.ErrorCode;
import com.simple.app.exception.SimpleSnsApplicationException;
import com.simple.app.model.entity.PostEntity;
import com.simple.app.model.entity.UserEntity;
import com.simple.app.repository.PostEntityRepository;
import com.simple.app.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;

    @Transactional
    public void create(String title, String body, String userName) {
        // user find
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() ->
                new SimpleSnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s is not found", userName)));

        // post save
        postEntityRepository.save(PostEntity.of(title, body, userEntity));
    }
}
