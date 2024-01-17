package com.simple.app.controller;

import com.simple.app.fixture.UserFixture;
import com.simple.app.model.User;
import com.simple.app.model.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;

public class WithMocCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithCustomUser> {
    @Override
    public SecurityContext createSecurityContext(WithCustomUser annotation) {
        String username = annotation.username();
        String password = annotation.password();
        UserRole userRole = annotation.role();

        User user = UserFixture.get(username, password, userRole);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                user, null, List.of(new SimpleGrantedAuthority(userRole.toString()))
        );

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(token);

        return securityContext;
    }
}
