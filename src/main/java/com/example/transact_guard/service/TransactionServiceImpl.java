package com.example.transact_guard.service;

import com.example.transact_guard.model.Transaction;
import com.example.transact_guard.model.User;
import com.example.transact_guard.repository.TransactionRepository;
import com.example.transact_guard.repository.UserRepository;
import com.example.transact_guard.service.FraudDetectionService;
import com.example.transact_guard.model.FraudTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final FraudDetectionService fraudDetectionService;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, UserRepository userRepository, FraudDetectionService fraudDetectionService) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.fraudDetectionService = fraudDetectionService;
    }

    @Override
    @Transactional
    public Transaction sendMoney(String senderId, String recipientUsername, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        Optional<User> senderOpt = userRepository.findById(senderId);
        Optional<User> recipientOpt = userRepository.findByUsername(recipientUsername);
        if (senderOpt.isEmpty() || recipientOpt.isEmpty()) {
            throw new IllegalArgumentException("Sender or recipient not found");
        }
        User sender = senderOpt.get();
        User recipient = recipientOpt.get();
        if (sender.getAccountBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        // Update balances
        sender.setAccountBalance(sender.getAccountBalance().subtract(amount));
        recipient.setAccountBalance(recipient.getAccountBalance().add(amount));
        userRepository.save(sender);
        userRepository.save(recipient);
        // Create transaction
        Transaction txn = new Transaction(null, sender.getUserId(), recipient.getUserId(), amount, new Date());
        Transaction savedTxn = transactionRepository.save(txn);
        // Evaluate for fraud after saving
        fraudDetectionService.evaluateTransaction(savedTxn);
        return savedTxn;
    }

    @Override
    public List<Transaction> getTransactionsByUserId(String userId) {
        return transactionRepository.findByUserId(userId);
    }

    @Override
    public List<Transaction> getTransactionsByRecipientId(String recipientId) {
        return transactionRepository.findByRecipientId(recipientId);
    }
} 