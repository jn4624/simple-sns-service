package com.simple.app.service;

import com.simple.app.exception.ErrorCode;
import com.simple.app.exception.SimpleSnsApplicationException;
import com.simple.app.fixture.PostEntityFixture;
import com.simple.app.fixture.UserEntityFixture;
import com.simple.app.model.entity.PostEntity;
import com.simple.app.model.entity.UserEntity;
import com.simple.app.repository.LikeEntityRepository;
import com.simple.app.repository.PostEntityRepository;
import com.simple.app.repository.UserEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PostServiceTest {
    @Autowired
    private PostService postService;

    @MockBean
    private PostEntityRepository postEntityRepository;

    @MockBean
    private UserEntityRepository userEntityRepository;

    @MockBean
    private LikeEntityRepository likeEntityRepository;

    @Test
    void 포스트_작성이_성공한_경우() {
        String title = "title";
        String body = "body";
        String userName = "userName";

        // mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(mock(UserEntity.class)));
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        Assertions.assertDoesNotThrow(() -> postService.create(title, body, userName));
    }

    @Test
    void 회원가입_되지_않은_사용자가_포스트를_작성하는_경우() {
        String title = "title";
        String body = "body";
        String userName = "userName";

        // mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        SimpleSnsApplicationException e = Assertions.assertThrows(SimpleSnsApplicationException.class, () -> postService.create(title, body, userName));
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 포스트_수정이_성공한_경우() {
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        // mocking
        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));
        when(postEntityRepository.saveAndFlush(any())).thenReturn(postEntity);

        Assertions.assertDoesNotThrow(() -> postService.modify(title, body, userName, postId));
    }

    @Test
    void 수정하려는_포스트가_존재하지_않는_경우() {
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        // mocking
        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

        SimpleSnsApplicationException e = Assertions.assertThrows(SimpleSnsApplicationException.class, () -> postService.modify(title, body, userName, postId));
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 수정하려는_포스트에_권한이_없는_경우() {
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        // mocking
        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity writer = UserEntityFixture.get("userName1", "password1", 2);

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(writer));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        SimpleSnsApplicationException e = Assertions.assertThrows(SimpleSnsApplicationException.class, () -> postService.modify(title, body, userName, postId));
        Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
    }

    @Test
    void 포스트_삭제가_성공한_경우() {
        String userName = "userName";
        Integer postId = 1;

        // mocking
        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        Assertions.assertDoesNotThrow(() -> postService.delete(userName, postId));
    }

    @Test
    void 삭제하려는_포스트가_존재하지_않는_경우() {
        String userName = "userName";
        Integer postId = 1;

        // mocking
        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

        SimpleSnsApplicationException e = Assertions.assertThrows(SimpleSnsApplicationException.class, () -> postService.delete(userName, postId));
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 삭제하려는_포스트에_권한이_없는_경우() {
        String userName = "userName";
        Integer postId = 1;

        // mocking
        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity writer = UserEntityFixture.get("userName1", "password1", 2);

        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(writer));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        SimpleSnsApplicationException e = Assertions.assertThrows(SimpleSnsApplicationException.class, () -> postService.delete(userName, postId));
        Assertions.assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
    }

    @Test
    void 피드_목록_요청이_성공한_경우() {
        // mocking
        Pageable pageable = mock(Pageable.class);
        when(postEntityRepository.findAll(pageable)).thenReturn(Page.empty());

        Assertions.assertDoesNotThrow(() -> postService.list(pageable));
    }

    @Test
    void 나의_피드_목록_요청이_성공한_경우() {
        // mocking
        Pageable pageable = mock(Pageable.class);
        UserEntity userEntity = mock(UserEntity.class);

        when(userEntityRepository.findByUserName(any())).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findAllByUser(pageable, userEntity)).thenReturn(Page.empty());

        Assertions.assertDoesNotThrow(() -> postService.my(pageable, ""));
    }

    @Test
    void 좋아요_요청_성공한경우() {
        PostEntity postEntity = mock(PostEntity.class);
        UserEntity userEntity = mock(UserEntity.class);

        when(postEntityRepository.findById(any())).thenReturn(Optional.of(postEntity));
        when(userEntityRepository.findByUserName(any())).thenReturn(Optional.of(userEntity));
        when(likeEntityRepository.findByUserAndPost(userEntity, postEntity)).thenReturn(Optional.empty());

        Assertions.assertDoesNotThrow(() -> postService.like(1, ""));
    }

    @Test
    void 로그인하지_않은_사용자가_좋아요_요청하는_경우() {
        PostEntity postEntity = mock(PostEntity.class);

        when(postEntityRepository.findById(any())).thenReturn(Optional.of(postEntity));
        when(userEntityRepository.findByUserName(any())).thenReturn(Optional.empty());

        SimpleSnsApplicationException e = Assertions.assertThrows(SimpleSnsApplicationException.class, () -> postService.like(1, ""));
        Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 좋아요하려는_포스트가_존재하지_않는_경우() {
        when(postEntityRepository.findById(any())).thenReturn(Optional.empty());

        SimpleSnsApplicationException e = Assertions.assertThrows(SimpleSnsApplicationException.class, () -> postService.like(1, ""));
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 좋아요_갯수_요청_성공한_경우() {
        PostEntity postEntity = mock(PostEntity.class);

        when(postEntityRepository.findById(any())).thenReturn(Optional.of(postEntity));

        Assertions.assertDoesNotThrow(() -> postService.likeCount(1));
    }

    @Test
    void 좋아요_갯수_요청_실패한_경우() {
        when(postEntityRepository.findById(any())).thenReturn(Optional.empty());

        SimpleSnsApplicationException e = Assertions.assertThrows(SimpleSnsApplicationException.class, () -> postService.likeCount(1));
        Assertions.assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }
}
