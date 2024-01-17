package com.simple.app.repository;

import com.simple.app.model.entity.LikeEntity;
import com.simple.app.model.entity.PostEntity;
import com.simple.app.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface LikeEntityRepository extends JpaRepository<LikeEntity, Integer> {
    Optional<LikeEntity> findByUserAndPost(UserEntity userEntity, PostEntity postEntity);

    long countByPost(PostEntity postEntity);

    @Transactional
    @Modifying
    @Query("UPDATE LikeEntity entity SET entity.deletedAt = NOW() WHERE entity.post = :post")
    void deleteAllByPost(@Param("post") PostEntity postEntity);
}
