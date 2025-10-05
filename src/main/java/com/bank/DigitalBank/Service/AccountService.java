package com.bank.DigitalBank.Service;


import com.bank.DigitalBank.Entity.enums.TransactionType;
import com.bank.DigitalBank.dto.TransferResponse;
import com.bank.DigitalBank.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public interface AccountService {

    ApiResponse<AccountDto> register(AccountDto account);

    ApiResponse<DepositResponseDTO> depositCash(String accountNumber, BigDecimal amount);

    ApiResponse<WithdrawResponseDTO> withdrawCash(String accountNumber, BigDecimal amount);

    ApiResponse<TransferResponse> transferAmount(String fromAccount, String toAccount, BigDecimal amount);

    ApiResponse<BalanceDTO> getBalance(String accountNumber);

    ApiResponse<Page<TrasactionResponse>> getTransactionHistory(String accountNumber, int size, int page);

    ApiResponse<AccountSummaryDTO> getAccountSummary(String accountNumber);

    ApiResponse<StatementResponse> getStatement(String accountNumber);

    ApiResponse<AdminDashboardResponse> getAdminDashboard();

    void toTransactionCSV(String accountNumber, LocalDate fromDate, LocalDate toDate, PrintWriter writer);


    ApiResponse<List<TransactionDTO>> searchTransactionByTypeAndDate(String accountNumber, TransactionType type, LocalDate from, LocalDate to);

    ApiResponse<List<InterestResponse>> getInterestTransaction();

    ApiResponse<BalanceDTO> getDollarBalance(String accountNumber);
}
