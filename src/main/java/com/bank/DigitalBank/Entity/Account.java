package com.bank.DigitalBank.Entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

@Entity

@Builder
@Table(name = "AccountDetails")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(unique = true, length = 12, name = "accountNumber")
    private String accountNumber = generateAccountNumberWithPrefix();

    @Column(nullable = false)
    @ColumnDefault(value = "0")
    private BigDecimal balance;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne
    private User user;


    private String generateAccountNumberWithPrefix() {
        String prefix = "BOK"; // 3 characters
        int digitsNeeded = 12 - prefix.length(); // 9 digits
        Random random = new Random();
        StringBuilder sb = new StringBuilder(prefix);

        for (int i = 0; i < digitsNeeded; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString(); // e.g., "BOK928374652"
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Account(Long id, String accountNumber, BigDecimal balance, LocalDateTime createdAt, User user) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.createdAt = createdAt;
        this.user = user;
    }

    public Account() {
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
