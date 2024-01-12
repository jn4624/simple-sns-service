package com.simple.app.repository;

import com.simple.app.model.entity.LikeEntity;
import com.simple.app.model.entity.PostEntity;
import com.simple.app.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LikeEntityRepository extends JpaRepository<LikeEntity, Integer> {
    Optional<LikeEntity> findByUserAndPost(UserEntity userEntity, PostEntity postEntity);

    @Query(value = "SELECT COUNT(*) FROM LikeEntity entity WHERE entity.post = :postEntity")
    Integer countByPost(PostEntity postEntity);
}
