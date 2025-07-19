package com.example.transact_guard.controller;

import com.example.transact_guard.model.User;
import com.example.transact_guard.model.FraudTransaction;
import com.example.transact_guard.repository.FraudTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class FraudAlertController {

    private final FraudTransactionRepository fraudTransactionRepository;

    @Autowired
    public FraudAlertController(FraudTransactionRepository fraudTransactionRepository) {
        this.fraudTransactionRepository = fraudTransactionRepository;
    }

    @GetMapping("/fraud-alerts")
    public String showFraudAlerts(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            model.addAttribute("error", "Please log in to view fraud alerts.");
            return "login";
        }
        List<FraudTransaction> frauds = fraudTransactionRepository.findByUserId(user.getUserId());
        model.addAttribute("user", user);
        model.addAttribute("frauds", frauds);
        return "fraud-alerts";
    }
} 