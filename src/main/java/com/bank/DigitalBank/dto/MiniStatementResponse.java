package com.bank.DigitalBank.dto;

import com.bank.DigitalBank.Entity.enums.TransactionType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MiniStatementResponse {

    private Long ID;

    private com.bank.DigitalBank.Entity.enums.TransactionType TransactionType;

    private BigDecimal amount;

    private String description;

    private BigDecimal balanceAfterTransaction;

    private LocalDateTime TransactionDate;

    public com.bank.DigitalBank.Entity.enums.TransactionType getTransactionType() {
        return TransactionType;
    }

    public void setTransactionType(com.bank.DigitalBank.Entity.enums.TransactionType transactionType) {
        TransactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBalanceAfterTransaction() {
        return balanceAfterTransaction;
    }

    public void setBalanceAfterTransaction(BigDecimal balanceAfterTransaction) {
        this.balanceAfterTransaction = balanceAfterTransaction;
    }

    public MiniStatementResponse(Long ID, com.bank.DigitalBank.Entity.enums.TransactionType transactionType, BigDecimal amount, String description, BigDecimal balanceAfterTransaction, LocalDateTime transactionDate) {
        this.ID = ID;
        TransactionType = transactionType;
        this.amount = amount;
        this.description = description;
        this.balanceAfterTransaction = balanceAfterTransaction;
        TransactionDate = transactionDate;
    }

    public MiniStatementResponse() {
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }




    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    public LocalDateTime getTransactionDate() {
        return TransactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        TransactionDate = transactionDate;
    }
}
