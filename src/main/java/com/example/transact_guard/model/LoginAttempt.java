package com.example.transact_guard.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "login_attempt")
public class LoginAttempt {
    @Id
    private String id;
    private String userId;
    private Date timestamp;
    private boolean success;
    private String type; // "login" or "transaction"
} 