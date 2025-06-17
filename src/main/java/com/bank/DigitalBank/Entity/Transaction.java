package com.bank.DigitalBank.Entity;

import com.bank.DigitalBank.Entity.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CurrentTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity

@Table(name = "TransactionDetails")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String fromAccount;

    @Column(nullable = false)
    private String toAccount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Column(nullable = false)
    private BigDecimal amount;


    @CurrentTimestamp
    @Column(nullable = false)
    private LocalDateTime transactionDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }


    public Transaction(Long id, String fromAccount, String toAccount, TransactionType transactionType, BigDecimal amount, LocalDateTime transactionDate) {
        this.id = id;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.transactionType = transactionType;
        this.amount = amount;
        this.transactionDate = transactionDate;
    }

    public Transaction() {
    }
}
