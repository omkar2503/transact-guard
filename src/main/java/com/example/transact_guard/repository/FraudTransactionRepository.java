package com.example.transact_guard.repository;

import com.example.transact_guard.model.FraudTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface FraudTransactionRepository extends MongoRepository<FraudTransaction, String> {
    List<FraudTransaction> findByUserId(String userId);
} 