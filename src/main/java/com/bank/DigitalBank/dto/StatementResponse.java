package com.bank.DigitalBank.dto;

import java.util.List;

public class StatementResponse {

    List<MiniStatementResponse> miniStatementResponse;

    public StatementResponse(List<MiniStatementResponse> miniStatementResponse) {
        this.miniStatementResponse = miniStatementResponse;
    }

    public StatementResponse() {
    }

    public List<MiniStatementResponse> getMiniStatementResponse() {
        return miniStatementResponse;
    }

    public void setMiniStatementResponse(List<MiniStatementResponse> miniStatementResponse) {
        this.miniStatementResponse = miniStatementResponse;
    }
}
