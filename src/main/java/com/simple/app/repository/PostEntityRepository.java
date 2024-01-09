package com.simple.app.repository;

import com.simple.app.model.entity.PostEntity;
import com.simple.app.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostEntityRepository extends JpaRepository<PostEntity, Integer> {
    Page<PostEntity> findAllByUser(Pageable pageable, UserEntity userEntity);
}
