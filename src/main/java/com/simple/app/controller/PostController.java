package com.simple.app.controller;

import com.simple.app.controller.request.PostCommentRequest;
import com.simple.app.controller.request.PostCreateRequest;
import com.simple.app.controller.request.PostModifyRequest;
import com.simple.app.controller.response.CommentResponse;
import com.simple.app.controller.response.PostResponse;
import com.simple.app.controller.response.Response;
import com.simple.app.model.Post;
import com.simple.app.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    public Response<Void> create(@RequestBody PostCreateRequest postCreateRequest, Authentication authentication) {
        postService.create(postCreateRequest.getTitle(), postCreateRequest.getBody(), authentication.getName());
        return Response.success();
    }

    @PutMapping("/{postId}")
    public Response<PostResponse> modify(@PathVariable Integer postId, @RequestBody PostModifyRequest postModifyRequest, Authentication authentication) {
        Post post = postService.modify(postModifyRequest.getTitle(), postModifyRequest.getBody(), authentication.getName(), postId);
        return Response.success(PostResponse.fromPost(post));
    }

    @DeleteMapping("/{postId}")
    public Response<Void> delete(@PathVariable Integer postId, Authentication authentication) {
        postService.delete(authentication.getName(), postId);
        return Response.success();
    }

    @GetMapping
    public Response<Page<PostResponse>> list(Pageable pageable, Authentication authentication) {
        return Response.success(postService.list(pageable).map(PostResponse::fromPost));
    }

    @GetMapping("/my")
    public Response<Page<PostResponse>> my(Pageable pageable, Authentication authentication) {
        return Response.success(postService.my(pageable, authentication.getName()).map(PostResponse::fromPost));
    }

    @PostMapping("/{postId}/likes")
    public Response<Void> like(@PathVariable Integer postId, Authentication authentication) {
        postService.like(postId, authentication.getName());
        return Response.success();
    }

//    @GetMapping("/{postId}/likes")
//    public Response<Integer> likeCount(@PathVariable Integer postId, Authentication authentication) {
//        return Response.success(postService.likeCount(postId));
//    }

    @GetMapping("/{postId}/likes")
    public Response<Long> likeCount(@PathVariable Integer postId, Authentication authentication) {
        return Response.success(postService.likeCount(postId));
    }

    @PostMapping("/{postId}/comments")
    public Response<Void> comment(@PathVariable Integer postId, @RequestBody PostCommentRequest postCommentRequest, Authentication authentication) {
        postService.comment(postId, postCommentRequest.getComment(), authentication.getName());
        return Response.success();
    }

    @GetMapping("/{postId}/comments")
    public Response<Page<CommentResponse>> commentList(@PathVariable Integer postId, Pageable pageable, Authentication authentication) {
        return Response.success(postService.commentList(postId, pageable).map(CommentResponse::fromComment));
    }
}
