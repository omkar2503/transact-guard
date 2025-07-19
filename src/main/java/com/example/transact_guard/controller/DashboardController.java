package com.example.transact_guard.controller;

import com.example.transact_guard.model.User;
import com.example.transact_guard.model.Transaction;
import com.example.transact_guard.model.FraudTransaction;
import com.example.transact_guard.service.UserService;
import com.example.transact_guard.service.TransactionService;
import com.example.transact_guard.repository.FraudTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.ArrayList;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DashboardController {

    private final UserService userService;
    private final TransactionService transactionService;
    private final FraudTransactionRepository fraudTransactionRepository;

    @Autowired
    public DashboardController(UserService userService, TransactionService transactionService, FraudTransactionRepository fraudTransactionRepository) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.fraudTransactionRepository = fraudTransactionRepository;
    }

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date startDate,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date endDate,
        @RequestParam(required = false) String recipient,
        @RequestParam(required = false) java.math.BigDecimal minAmount,
        @RequestParam(required = false) java.math.BigDecimal maxAmount) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            model.addAttribute("error", "Please log in.");
            return "login";
        }
        // Adjust endDate to 23:59:59 if set
        java.util.Date adjustedEndDate = endDate;
        if (endDate != null) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(endDate);
            cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
            cal.set(java.util.Calendar.MINUTE, 59);
            cal.set(java.util.Calendar.SECOND, 59);
            cal.set(java.util.Calendar.MILLISECOND, 999);
            adjustedEndDate = cal.getTime();
        }
        boolean hasFilter = startDate != null || endDate != null || recipient != null || minAmount != null || maxAmount != null;
        String recipientId = null;
        if (recipient != null && !recipient.isEmpty()) {
            recipientId = userService.findByUsername(recipient).map(u -> u.getUserId()).orElse(null);
            if (recipientId == null) {
                model.addAttribute("user", currentUser);
                model.addAttribute("transactions", new ArrayList<>());
                model.addAttribute("frauds", new ArrayList<>());
                model.addAttribute("startDate", startDate);
                model.addAttribute("endDate", endDate);
                model.addAttribute("recipient", recipient);
                model.addAttribute("minAmount", minAmount);
                model.addAttribute("maxAmount", maxAmount);
                return "dashboard";
            }
        }
        List<Transaction> transactions = hasFilter
            ? transactionService.filterTransactions(currentUser.getUserId(), startDate, adjustedEndDate, recipient, minAmount, maxAmount)
            : transactionService.getTransactionsByUserId(currentUser.getUserId());
        List<FraudTransaction> frauds = fraudTransactionRepository.findByUserId(currentUser.getUserId());
        model.addAttribute("user", currentUser);
        model.addAttribute("transactions", transactions != null ? transactions : new ArrayList<>());
        model.addAttribute("frauds", frauds != null ? frauds : new ArrayList<>());
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("recipient", recipient);
        model.addAttribute("minAmount", minAmount);
        model.addAttribute("maxAmount", maxAmount);
        return "dashboard";
    }
} 