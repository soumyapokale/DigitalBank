package com.bank.DigitalBank.dto;

import java.math.BigDecimal;

public class AdminDashboardResponse {
    private long totalAccounts;
    private long totalUsers;
    private BigDecimal totalBalance;
    private long totalTransactions;
    private long totalDeposits;
    private long totalWithdrawals;
    private long totalInterestCredited;

    // Constructor, Getters, Setters


    public AdminDashboardResponse(long totalAccounts, long totalUsers, BigDecimal totalBalance, long totalTransactions, long totalDeposits, long totalWithdrawals, long totalInterestCredited) {
        this.totalAccounts = totalAccounts;
        this.totalUsers = totalUsers;
        this.totalBalance = totalBalance;
        this.totalTransactions = totalTransactions;
        this.totalDeposits = totalDeposits;
        this.totalWithdrawals = totalWithdrawals;
        this.totalInterestCredited = totalInterestCredited;
    }

    public AdminDashboardResponse() {
    }

    public long getTotalAccounts() {
        return totalAccounts;
    }

    public void setTotalAccounts(long totalAccounts) {
        this.totalAccounts = totalAccounts;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(BigDecimal totalBalance) {
        this.totalBalance = totalBalance;
    }

    public long getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(long totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public long getTotalDeposits() {
        return totalDeposits;
    }

    public void setTotalDeposits(long totalDeposits) {
        this.totalDeposits = totalDeposits;
    }

    public long getTotalWithdrawals() {
        return totalWithdrawals;
    }

    public void setTotalWithdrawals(long totalWithdrawals) {
        this.totalWithdrawals = totalWithdrawals;
    }

    public long getTotalInterestCredited() {
        return totalInterestCredited;
    }

    public void setTotalInterestCredited(long totalInterestCredited) {
        this.totalInterestCredited = totalInterestCredited;
    }
}
