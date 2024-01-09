package com.simple.app.controller;

import com.simple.app.controller.request.PostCreateRequest;
import com.simple.app.controller.response.Response;
import com.simple.app.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
