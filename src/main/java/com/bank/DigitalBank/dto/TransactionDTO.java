package com.bank.DigitalBank.dto;

import com.bank.DigitalBank.Entity.enums.TransactionType;
import jakarta.persistence.*;
import org.hibernate.annotations.CurrentTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;


import com.bank.DigitalBank.Entity.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDTO {

    private Long id;
    private String fromAccount;
    private String toAccount;
    private TransactionType transactionType;
    private BigDecimal amount;
    private BigDecimal savedBalanceAfterTransaction;
    private LocalDateTime transactionDate;

    // Constructors
    public TransactionDTO() {
    }

    public TransactionDTO(Long id, String fromAccount, String toAccount, TransactionType transactionType,
                          BigDecimal amount, BigDecimal savedBalanceAfterTransaction, LocalDateTime transactionDate) {
        this.id = id;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.transactionType = transactionType;
        this.amount = amount;
        this.savedBalanceAfterTransaction = savedBalanceAfterTransaction;
        this.transactionDate = transactionDate;
    }

    // Getters and Setters

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

    public BigDecimal getSavedBalanceAfterTransaction() {
        return savedBalanceAfterTransaction;
    }

    public void setSavedBalanceAfterTransaction(BigDecimal savedBalanceAfterTransaction) {
        this.savedBalanceAfterTransaction = savedBalanceAfterTransaction;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
}
