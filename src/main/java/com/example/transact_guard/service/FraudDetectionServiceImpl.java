package com.example.transact_guard.service;

import com.example.transact_guard.model.FraudTransaction;
import com.example.transact_guard.model.Transaction;
import com.example.transact_guard.model.User;
import com.example.transact_guard.model.LoginAttempt;
import com.example.transact_guard.repository.FraudTransactionRepository;
import com.example.transact_guard.repository.TransactionRepository;
import com.example.transact_guard.repository.UserRepository;
import com.example.transact_guard.repository.LoginAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FraudDetectionServiceImpl implements FraudDetectionService {

    private final TransactionRepository transactionRepository;
    private final FraudTransactionRepository fraudTransactionRepository;
    private final UserRepository userRepository;
    private final LoginAttemptRepository loginAttemptRepository;

    @Autowired
    public FraudDetectionServiceImpl(TransactionRepository transactionRepository,
                                     FraudTransactionRepository fraudTransactionRepository,
                                     UserRepository userRepository,
                                     LoginAttemptRepository loginAttemptRepository) {
        this.transactionRepository = transactionRepository;
        this.fraudTransactionRepository = fraudTransactionRepository;
        this.userRepository = userRepository;
        this.loginAttemptRepository = loginAttemptRepository;
    }

    @Override
    public List<FraudTransaction> evaluateTransaction(Transaction transaction) {
        List<FraudTransaction> frauds = new ArrayList<>();
        String userId = transaction.getUserId();
        String recipientId = transaction.getRecipientId();
        BigDecimal amount = transaction.getAmount();
        Date now = transaction.getTimestamp();

        // 1. High Amount Fraud: Amount > 3× average
        List<Transaction> userTxns = transactionRepository.findByUserId(userId);
        if (!userTxns.isEmpty()) {
            BigDecimal avg = userTxns.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(userTxns.size()), BigDecimal.ROUND_HALF_UP);
            if (amount.compareTo(avg.multiply(BigDecimal.valueOf(3))) > 0) {
                frauds.add(new FraudTransaction(null, transaction.getId(), userId, recipientId, "High Amount Fraud", now));
            }
        }

        // 2. Velocity Fraud: >10 txns + total > ₹10,000 in 1 hour
        Date oneHourAgo = new Date(now.getTime() - 60 * 60 * 1000);
        List<Transaction> lastHourTxns = userTxns.stream()
            .filter(t -> t.getTimestamp().after(oneHourAgo))
            .collect(Collectors.toList());
        BigDecimal lastHourTotal = lastHourTxns.stream()
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (lastHourTxns.size() > 10 && lastHourTotal.compareTo(BigDecimal.valueOf(10000)) > 0) {
            frauds.add(new FraudTransaction(null, transaction.getId(), userId, recipientId, "Velocity Fraud", now));
        }

        // 3. Unusual Pattern: Odd hour + new recipient
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        boolean oddHour = (hour < 6 || hour > 22);
        boolean newRecipient = userTxns.stream().noneMatch(t -> t.getRecipientId().equals(recipientId));
        if (oddHour && newRecipient) {
            frauds.add(new FraudTransaction(null, transaction.getId(), userId, recipientId, "Unusual Pattern: Odd hour + new recipient", now));
        }

        // 4. Frequency Spike: Hourly rate > 3× weekly avg
        Date weekAgo = new Date(now.getTime() - 7L * 24 * 60 * 60 * 1000);
        List<Transaction> weekTxns = userTxns.stream()
            .filter(t -> t.getTimestamp().after(weekAgo))
            .collect(Collectors.toList());
        Map<Integer, Long> hourlyCounts = weekTxns.stream()
            .collect(Collectors.groupingBy(t -> {
                Calendar c = Calendar.getInstance();
                c.setTime(t.getTimestamp());
                return c.get(Calendar.HOUR_OF_DAY);
            }, Collectors.counting()));
        long thisHourCount = hourlyCounts.getOrDefault(hour, 0L);
        double weeklyAvg = weekTxns.size() / 7.0 / 24.0;
        if (weeklyAvg > 0 && thisHourCount > 3 * weeklyAvg) {
            frauds.add(new FraudTransaction(null, transaction.getId(), userId, recipientId, "Frequency Spike", now));
        }

        // 5. Duplicate Transaction Rule: Same sender→recipient, same amount, within 5 mins
        Date fiveMinsAgo = new Date(now.getTime() - 5 * 60 * 1000);
        boolean duplicate = userTxns.stream()
            .filter(t -> t.getRecipientId().equals(recipientId))
            .filter(t -> t.getAmount().compareTo(amount) == 0)
            .anyMatch(t -> t.getTimestamp().after(fiveMinsAgo));
        if (duplicate) {
            frauds.add(new FraudTransaction(null, transaction.getId(), userId, recipientId, "Duplicate Transaction", now));
        }

        // 6. Failed Attempts + High-Value Transaction Rule
        Date thirtyMinsAgo = new Date(now.getTime() - 30 * 60 * 1000);
        List<LoginAttempt> recentFails = loginAttemptRepository.findByUserId(userId).stream()
            .filter(a -> !a.isSuccess() && a.getTimestamp().after(thirtyMinsAgo))
            .collect(Collectors.toList());
        if (recentFails.size() >= 3 && amount.compareTo(BigDecimal.valueOf(20000)) > 0) {
            frauds.add(new FraudTransaction(null, transaction.getId(), userId, recipientId, "Failed Attempts + High-Value Transaction", now));
        }

        // Log all detected frauds
        frauds.forEach(this::logFraud);
        return frauds;
    }

    @Override
    public void logFraud(FraudTransaction fraudTransaction) {
        fraudTransactionRepository.save(fraudTransaction);
    }
} 