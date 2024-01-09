package com.simple.app.controller;

import com.simple.app.controller.request.PostCreateRequest;
import com.simple.app.controller.request.PostModifyRequest;
import com.simple.app.controller.response.PostResponse;
import com.simple.app.controller.response.Response;
import com.simple.app.model.Post;
import com.simple.app.service.PostService;
import lombok.RequiredArgsConstructor;
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
}
