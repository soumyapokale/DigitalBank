package com.bank.DigitalBank.Service;


import com.bank.DigitalBank.DTO.TransferResponse;
import com.bank.DigitalBank.Entity.Account;
import com.bank.DigitalBank.dto.ApiResponse;
import com.bank.DigitalBank.dto.BalanceDTO;
import com.bank.DigitalBank.dto.DepositResponseDTO;
import com.bank.DigitalBank.dto.WithdrawResponseDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface AccountService {

    ApiResponse<Account> register(Account account);

    ApiResponse<DepositResponseDTO> depositCash(String accountNumber, BigDecimal amount);

    ApiResponse<WithdrawResponseDTO> withdrawCash(String accountNumber, BigDecimal amount);

    ApiResponse<TransferResponse> transferAmount(String fromAccount, String toAccount, BigDecimal amount);

    ApiResponse<BalanceDTO> getBalance(String accountNumber);
}
