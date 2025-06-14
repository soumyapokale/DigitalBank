package com.bank.DigitalBank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;


public class TransferResponse {
    private String fromAccount;
    private String toAccount;
    private BigDecimal transferredAmount;

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

    public BigDecimal getTransferredAmount() {
        return transferredAmount;
    }

    public void setTransferredAmount(BigDecimal transferredAmount) {
        this.transferredAmount = transferredAmount;
    }

    public TransferResponse(String fromAccount, String toAccount, BigDecimal transferredAmount) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.transferredAmount = transferredAmount;
    }

    public TransferResponse() {
    }
}
