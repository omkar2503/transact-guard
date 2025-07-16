package com.example.transact_guard.controller;

import com.example.transact_guard.model.User;
import com.example.transact_guard.model.Transaction;
import com.example.transact_guard.service.TransactionService;
import com.example.transact_guard.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.math.BigDecimal;
import java.util.Optional;

@Controller
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    @Autowired
    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @GetMapping("/transfer")
    public String showTransferForm(@ModelAttribute("user") User user, Model model) {
        if (user == null) {
            model.addAttribute("error", "Please log in to transfer money.");
            return "login";
        }
        model.addAttribute("user", user);
        return "transfer";
    }

    @PostMapping("/transfer")
    public String transferMoney(@ModelAttribute("user") User user,
                                @RequestParam String recipientUsername,
                                @RequestParam BigDecimal amount,
                                Model model) {
        if (user == null) {
            model.addAttribute("error", "Please log in to transfer money.");
            return "login";
        }
        try {
            Transaction txn = transactionService.sendMoney(user.getUserId(), recipientUsername, amount);
            model.addAttribute("success", "Transfer successful! Transaction ID: " + txn.getId());
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("user", userService.findById(user.getUserId()).orElse(user));
        return "transfer";
    }
} 