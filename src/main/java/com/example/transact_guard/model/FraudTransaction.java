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
@Document(collection = "fraud_transaction")
public class FraudTransaction {
    @Id
    private String id;
    private String transactionId;
    private String userId;
    private String recipientId;
    private String description;
    private Date timestamp;
} 