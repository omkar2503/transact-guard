package com.example.transact_guard.repository;

import com.example.transact_guard.model.LoginAttempt;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface LoginAttemptRepository extends MongoRepository<LoginAttempt, String> {
    List<LoginAttempt> findByUserId(String userId);
} 