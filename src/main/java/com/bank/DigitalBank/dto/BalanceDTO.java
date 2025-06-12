package com.bank.DigitalBank.dto;

import jakarta.persistence.Column;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

public class BalanceDTO {


    private  String accountNumber;


    private BigDecimal balance;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BalanceDTO(String accountNumber, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public BalanceDTO() {
    }
}
