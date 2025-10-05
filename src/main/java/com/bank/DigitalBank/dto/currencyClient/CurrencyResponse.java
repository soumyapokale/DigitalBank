package com.bank.DigitalBank.dto.currencyClient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class CurrencyResponse {
    private double convertedAmount;
    private double rate;

    public CurrencyResponse() {
    }

    public CurrencyResponse(double convertedAmount, double rate) {
        this.convertedAmount = convertedAmount;
        this.rate = rate;
    }

    public double getConvertedAmount() {
        return convertedAmount;
    }

    public void setConvertedAmount(double convertedAmount) {
        this.convertedAmount = convertedAmount;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}