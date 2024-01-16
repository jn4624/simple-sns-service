package com.simple.app.repository;

import com.simple.app.model.entity.AlarmEntity;
import com.simple.app.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmEntityRepository extends JpaRepository<AlarmEntity, Integer> {
    Page<AlarmEntity> findAllByUser(UserEntity userEntity, Pageable pageable);
}
