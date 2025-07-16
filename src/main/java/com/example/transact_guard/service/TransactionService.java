package com.example.transact_guard.service;

import com.example.transact_guard.model.Transaction;
import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {
    Transaction sendMoney(String senderId, String recipientUsername, BigDecimal amount);
    List<Transaction> getTransactionsByUserId(String userId);
    List<Transaction> getTransactionsByRecipientId(String recipientId);
} 