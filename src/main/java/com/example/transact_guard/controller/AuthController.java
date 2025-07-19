package com.example.transact_guard.controller;

import com.example.transact_guard.model.User;
import com.example.transact_guard.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import java.math.BigDecimal;
import com.example.transact_guard.model.LoginAttempt;
import com.example.transact_guard.repository.LoginAttemptRepository;
import java.util.Date;

@Controller
public class AuthController {

    private final UserService userService;
    private final LoginAttemptRepository loginAttemptRepository;

    @Autowired
    public AuthController(UserService userService, LoginAttemptRepository loginAttemptRepository) {
        this.userService = userService;
        this.loginAttemptRepository = loginAttemptRepository;
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam BigDecimal initialBalance,
                               Model model,
                               HttpSession session) {
        try {
            User user = userService.registerUser(username, password, initialBalance);
            session.setAttribute("user", user);
            return "redirect:/dashboard";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String username,
                            @RequestParam String password,
                            Model model,
                            HttpSession session) {
        Optional<User> userOpt = userService.findByUsername(username);
        String userId = userOpt.map(User::getUserId).orElse(null);
        boolean success = false;
        if (userOpt.isPresent() && userService.authenticate(username, password).isPresent()) {
            success = true;
            session.setAttribute("user", userOpt.get());
        }
        // Only record attempts for existing users
        if (userId != null) {
            LoginAttempt attempt = new LoginAttempt(null, userId, new Date(), success, "login");
            loginAttemptRepository.save(attempt);
        }
        if (success) {
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
} 