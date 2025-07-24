package com.bank.DigitalBank.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InterestResponse {

    private String accountNumber;

    private BigDecimal creditedAmount;

    private LocalDateTime date;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getCreditedAmount() {
        return creditedAmount;
    }

    public void setCreditedAmount(BigDecimal creditedAmount) {
        this.creditedAmount = creditedAmount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public InterestResponse(String accountNumber, BigDecimal creditedAmount, LocalDateTime date) {
        this.accountNumber = accountNumber;
        this.creditedAmount = creditedAmount;
        this.date = date;
    }

    public InterestResponse() {
    }
}
