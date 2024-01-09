package com.simple.app.repository;

import com.simple.app.model.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostEntityRepository extends JpaRepository<PostEntity, Integer> {
}
