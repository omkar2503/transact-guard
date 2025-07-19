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
    public String showDashboard(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            model.addAttribute("error", "Please log in.");
            return "login";
        }
        List<Transaction> transactions = transactionService.getTransactionsByUserId(currentUser.getUserId());
        List<FraudTransaction> frauds = fraudTransactionRepository.findByUserId(currentUser.getUserId());
        model.addAttribute("user", currentUser);
        model.addAttribute("transactions", transactions != null ? transactions : new ArrayList<>());
        model.addAttribute("frauds", frauds != null ? frauds : new ArrayList<>());
        return "dashboard";
    }
} 