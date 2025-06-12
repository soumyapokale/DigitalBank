package com.bank.DigitalBank.dto;



import java.math.BigDecimal;


public class WithdrawResponseDTO {
    private String status;
    private String message;
    private String accountNumber;
    private BigDecimal newBalance;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(BigDecimal newBalance) {
        this.newBalance = newBalance;
    }

    public WithdrawResponseDTO(String status, String message, String accountNumber, BigDecimal newBalance) {
        this.status = status;
        this.message = message;
        this.accountNumber = accountNumber;
        this.newBalance = newBalance;
    }

    public WithdrawResponseDTO() {
    }
}

