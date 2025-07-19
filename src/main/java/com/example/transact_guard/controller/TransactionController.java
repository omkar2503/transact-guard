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
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import com.example.transact_guard.model.LoginAttempt;
import com.example.transact_guard.repository.LoginAttemptRepository;
import java.util.Date;

@Controller
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;
    private final LoginAttemptRepository loginAttemptRepository;

    @Autowired
    public TransactionController(TransactionService transactionService, UserService userService, LoginAttemptRepository loginAttemptRepository) {
        this.transactionService = transactionService;
        this.userService = userService;
        this.loginAttemptRepository = loginAttemptRepository;
    }

    @GetMapping("/transfer")
    public String showTransferForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            model.addAttribute("error", "Please log in to transfer money.");
            return "login";
        }
        model.addAttribute("user", user);
        return "transfer";
    }

    @PostMapping("/transfer")
    public String transferMoney(HttpSession session,
                                @RequestParam String recipientUsername,
                                @RequestParam BigDecimal amount,
                                Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            model.addAttribute("error", "Please log in to transfer money.");
            return "login";
        }
        try {
            Transaction txn = transactionService.sendMoney(user.getUserId(), recipientUsername, amount);
            model.addAttribute("success", "Transfer successful! Transaction ID: " + txn.getId());
            // Update user balance in session
            user = userService.findById(user.getUserId()).orElse(user);
            session.setAttribute("user", user);
        } catch (Exception e) {
            // Record failed transaction attempt
            LoginAttempt attempt = new LoginAttempt(null, user.getUserId(), new Date(), false, "transaction");
            loginAttemptRepository.save(attempt);
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("user", user);
        return "transfer";
    }
} 