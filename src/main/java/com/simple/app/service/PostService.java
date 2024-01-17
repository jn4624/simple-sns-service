package com.simple.app.service;

import com.simple.app.exception.ErrorCode;
import com.simple.app.exception.SimpleSnsApplicationException;
import com.simple.app.model.AlarmArgs;
import com.simple.app.model.AlarmType;
import com.simple.app.model.Comment;
import com.simple.app.model.Post;
import com.simple.app.model.entity.*;
import com.simple.app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private final LikeEntityRepository likeEntityRepository;
    private final CommentEntityRepository commentEntityRepository;
    private final AlarmEntityRepository alarmEntityRepository;

    private final AlarmService alarmService;

    @Transactional
    public void create(String title, String body, String userName) {
        UserEntity userEntity = getUserEntityOrException(userName);

        // post save
        postEntityRepository.save(PostEntity.of(title, body, userEntity));
    }

    @Transactional
    public Post modify(String title, String body, String userName, Integer postId) {
        UserEntity userEntity = getUserEntityOrException(userName);
        PostEntity postEntity = getPostEntityOrException(postId);

        // post permission
        if (postEntity.getUser() != userEntity) {
            throw new SimpleSnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %d", userName, postId));
        }

        postEntity.setTitle(title);
        postEntity.setBody(body);

        return Post.fromEntity(postEntityRepository.saveAndFlush(postEntity));
    }

    @Transactional
    public void delete(String userName, Integer postId) {
        UserEntity userEntity = getUserEntityOrException(userName);
        PostEntity postEntity = getPostEntityOrException(postId);

        // post permission
        if (postEntity.getUser() != userEntity) {
            throw new SimpleSnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %d", userName, postId));
        }

        likeEntityRepository.deleteAllByPost(postEntity);
        commentEntityRepository.deleteAllByPost(postEntity);
        postEntityRepository.delete(postEntity);
    }

    public Page<Post> list(Pageable pageable) {
        return postEntityRepository.findAll(pageable).map(Post::fromEntity);
    }

    public Page<Post> my(Pageable pageable, String userName) {
        UserEntity userEntity = getUserEntityOrException(userName);
        return postEntityRepository.findAllByUser(pageable, userEntity).map(Post::fromEntity);
    }

    @Transactional
    public void like(Integer postId, String userName) {
        PostEntity postEntity = getPostEntityOrException(postId);
        UserEntity userEntity = getUserEntityOrException(userName);

        // check liked -> throw
        likeEntityRepository.findByUserAndPost(userEntity, postEntity).ifPresent(it -> {
            throw new SimpleSnsApplicationException(ErrorCode.ALREADY_LIKED, String.format("userName %s already like post %d", userName, postId));
        });

        // like save
        likeEntityRepository.save(LikeEntity.of(userEntity, postEntity));

        // alarm save
        AlarmEntity alarmEntity = alarmEntityRepository.save(AlarmEntity.of(postEntity.getUser(), AlarmType.NEW_LIKE_ON_POST, new AlarmArgs(userEntity.getId(), postEntity.getId())));

        // sseEmitter
        alarmService.send(alarmEntity.getId(), postEntity.getUser().getId());
    }

    public long likeCount(Integer postId) {
        PostEntity postEntity = getPostEntityOrException(postId);
        return likeEntityRepository.countByPost(postEntity);
    }

    @Transactional
    public void comment(Integer postId, String comment, String userName) {
        PostEntity postEntity = getPostEntityOrException(postId);
        UserEntity userEntity = getUserEntityOrException(userName);

        // comment save
        commentEntityRepository.save(CommentEntity.of(userEntity, postEntity, comment));

        // alarm save
        AlarmEntity alarmEntity = alarmEntityRepository.save(AlarmEntity.of(postEntity.getUser(), AlarmType.NEW_COMMENT_ON_POST, new AlarmArgs(userEntity.getId(), postEntity.getId())));

        // sseEmitter
        alarmService.send(alarmEntity.getId(), postEntity.getUser().getId());
    }

    public Page<Comment> commentList(Integer postId, Pageable pageable) {
        PostEntity postEntity = getPostEntityOrException(postId);
        return commentEntityRepository.findAllByPost(postEntity, pageable).map(Comment::fromEntity);
    }

    private PostEntity getPostEntityOrException(Integer postId) {
        // post exist
        return postEntityRepository.findById(postId).orElseThrow(() ->
                new SimpleSnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%d not founded", postId)));
    }

    private UserEntity getUserEntityOrException(String userName) {
        // user exist
        return userEntityRepository.findByUserName(userName).orElseThrow(() ->
                new SimpleSnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));
    }
}
