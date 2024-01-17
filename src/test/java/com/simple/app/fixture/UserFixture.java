package com.simple.app.fixture;

import com.simple.app.model.User;
import com.simple.app.model.UserRole;

public class UserFixture {
    public static User get(String userName, String password, UserRole userRole) {
        User result = new User();
        result.setUsername(userName);
        result.setPassword(password);
        result.setUserRole(userRole);
        return result;
    }
}
