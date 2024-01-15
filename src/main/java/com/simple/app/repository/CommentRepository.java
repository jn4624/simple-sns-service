package com.simple.app.repository;

import com.simple.app.model.entity.CommentEntity;
import com.simple.app.model.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {
    Page<CommentEntity> findAllByPost(PostEntity postEntity, Pageable pageable);
}
