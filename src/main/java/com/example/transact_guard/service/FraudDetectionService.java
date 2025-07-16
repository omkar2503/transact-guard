package com.example.transact_guard.service;

import com.example.transact_guard.model.Transaction;
import com.example.transact_guard.model.FraudTransaction;
import java.util.List;

public interface FraudDetectionService {
    List<FraudTransaction> evaluateTransaction(Transaction transaction);
    void logFraud(FraudTransaction fraudTransaction);
} 