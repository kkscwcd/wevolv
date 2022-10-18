package com.auth.testlogin.integration.user.service;

import com.auth.testlogin.integration.user.modal.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findUserByEmail(String email);
}
