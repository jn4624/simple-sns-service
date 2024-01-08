package com.simple.app.controller;

import com.simple.app.controller.request.UserJoinRequest;
import com.simple.app.controller.request.UserLoginRequest;
import com.simple.app.controller.response.Response;
import com.simple.app.controller.response.UserJoinResponse;
import com.simple.app.controller.response.UserLoginResponse;
import com.simple.app.model.User;
import com.simple.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest userJoinRequest) {
        User user = userService.join(userJoinRequest.getUserName(), userJoinRequest.getPassword());
        return Response.success(UserJoinResponse.fromUser(user));
    }

    @PostMapping("login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest userLoginRequest) {
        String token = userService.login(userLoginRequest.getUserName(), userLoginRequest.getPassword());
        return Response.success(new UserLoginResponse(token));
    }
}
