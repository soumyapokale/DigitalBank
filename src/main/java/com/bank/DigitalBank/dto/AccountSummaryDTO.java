package com.bank.DigitalBank.dto;

import java.math.BigDecimal;

public class AccountSummaryDTO {


    private String accountNumber;

    private String holderName;

    private BigDecimal balance;

    private BigDecimal totalDeposits;

    private BigDecimal totalWithdrawals;

    private BigDecimal totalTransfers;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getTotalDeposits() {
        return totalDeposits;
    }

    public void setTotalDeposits(BigDecimal totalDeposits) {
        this.totalDeposits = totalDeposits;
    }

    public BigDecimal getTotalWithdrawals() {
        return totalWithdrawals;
    }

    public void setTotalWithdrawals(BigDecimal totalWithdrawals) {
        this.totalWithdrawals = totalWithdrawals;
    }

    public BigDecimal getTotalTransfers() {
        return totalTransfers;
    }

    public void setTotalTransfers(BigDecimal totalTransfers) {
        this.totalTransfers = totalTransfers;
    }

    public AccountSummaryDTO(String accountNumber, String holderName, BigDecimal balance, BigDecimal totalDeposits, BigDecimal totalWithdrawals, BigDecimal totalTransfers) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = balance;
        this.totalDeposits = totalDeposits;
        this.totalWithdrawals = totalWithdrawals;
        this.totalTransfers = totalTransfers;
    }

    public AccountSummaryDTO() {
    }
}
