package com.simple.app.controller;

import com.simple.app.controller.request.UserJoinRequest;
import com.simple.app.controller.request.UserLoginRequest;
import com.simple.app.controller.response.AlarmResponse;
import com.simple.app.controller.response.Response;
import com.simple.app.controller.response.UserJoinResponse;
import com.simple.app.controller.response.UserLoginResponse;
import com.simple.app.exception.ErrorCode;
import com.simple.app.exception.SimpleSnsApplicationException;
import com.simple.app.model.User;
import com.simple.app.service.UserService;
import com.simple.app.util.ClassUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest userJoinRequest) {
        User user = userService.join(userJoinRequest.getName(), userJoinRequest.getPassword());
        return Response.success(UserJoinResponse.fromUser(user));
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest userLoginRequest) {
        String token = userService.login(userLoginRequest.getName(), userLoginRequest.getPassword());
        return Response.success(new UserLoginResponse(token));
    }

    @GetMapping("/alarm")
    public Response<Page<AlarmResponse>> alarm(Pageable pageable, Authentication authentication) {
        User user = ClassUtils.getSafeCastInstance(authentication.getPrincipal(), User.class).orElseThrow(() ->
                new SimpleSnsApplicationException(ErrorCode.INTERNAL_SERVER_ERROR, "Casting to User class failed"));

        return Response.success(userService.alarmList(user.getId(), pageable).map(AlarmResponse::fromAlarm));
    }
}
