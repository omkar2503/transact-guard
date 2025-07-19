package com.example.transact_guard.repository;

import com.example.transact_guard.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;
import java.util.Date;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findByUserId(String userId);
    List<Transaction> findByRecipientId(String recipientId);

    @Query("{ 'userId': ?0, 'timestamp': { $gte: ?1, $lte: ?2 }, 'recipientId': { $regex: ?3, $options: 'i' }, 'amount': { $gte: ?4, $lte: ?5 } }")
    List<Transaction> filterTransactions(String userId, Date start, Date end, String recipientRegex, java.math.BigDecimal minAmount, java.math.BigDecimal maxAmount);
} 