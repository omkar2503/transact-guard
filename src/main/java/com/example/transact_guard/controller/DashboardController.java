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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

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
    public String showDashboard(@RequestParam(required = false) String userId, @ModelAttribute("user") User user, Model model) {
        // For now, get user from model or fallback to userId param
        User currentUser = user;
        if (currentUser == null && userId != null) {
            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isPresent()) {
                currentUser = userOpt.get();
            }
        }
        if (currentUser == null) {
            model.addAttribute("error", "User not found. Please log in.");
            return "login";
        }
        List<Transaction> transactions = transactionService.getTransactionsByUserId(currentUser.getUserId());
        List<FraudTransaction> frauds = fraudTransactionRepository.findByUserId(currentUser.getUserId());
        model.addAttribute("user", currentUser);
        model.addAttribute("transactions", transactions);
        model.addAttribute("frauds", frauds);
        return "dashboard";
    }
} 