package com.simple.app.model;

import com.simple.app.model.entity.CommentEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@AllArgsConstructor
public class Comment {
    private Integer id;
    private String comment;
    private String userName;
    private Integer postId;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static Comment fromEntity(CommentEntity commentEntity) {
        return new Comment(
                commentEntity.getId(),
                commentEntity.getComment(),
                commentEntity.getUser().getUserName(),
                commentEntity.getPost().getId(),
                commentEntity.getRegisteredAt(),
                commentEntity.getUpdatedAt(),
                commentEntity.getDeletedAt()
        );
    }
}
