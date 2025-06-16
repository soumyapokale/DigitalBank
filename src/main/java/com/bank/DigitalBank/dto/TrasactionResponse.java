package com.bank.DigitalBank.dto;

import com.bank.DigitalBank.Entity.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TrasactionResponse {

    private Long id;

    private TransactionType transactionType;


    private BigDecimal amount;



    private LocalDateTime transactionDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public TrasactionResponse(Long id, TransactionType transactionType, BigDecimal amount, LocalDateTime transactionDate) {
        this.id = id;
        this.transactionType = transactionType;
        this.amount = amount;
        this.transactionDate = transactionDate;
    }

    public TrasactionResponse() {
    }
}
