package com.example.transact_guard.service;

import com.example.transact_guard.model.Transaction;
import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {
    Transaction sendMoney(String senderId, String recipientUsername, BigDecimal amount);
    List<Transaction> getTransactionsByUserId(String userId);
    List<Transaction> getTransactionsByRecipientId(String recipientId);
    List<Transaction> filterTransactions(String userId, java.util.Date start, java.util.Date end, String recipientUsername, java.math.BigDecimal minAmount, java.math.BigDecimal maxAmount);
} 