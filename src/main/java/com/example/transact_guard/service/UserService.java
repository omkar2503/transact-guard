package com.example.transact_guard.service;

import com.example.transact_guard.model.User;
import java.util.Optional;

public interface UserService {
    User registerUser(String username, String password);
    Optional<User> authenticate(String username, String password);
    Optional<User> findByUsername(String username);
    Optional<User> findById(String userId);
} 