package com.bank.DigitalBank.Service;


import com.bank.DigitalBank.dto.TransferResponse;
import com.bank.DigitalBank.Entity.Account;
import com.bank.DigitalBank.dto.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface AccountService {

    ApiResponse<AccountDto> register(AccountDto account);

    ApiResponse<DepositResponseDTO> depositCash(String accountNumber, BigDecimal amount);

    ApiResponse<WithdrawResponseDTO> withdrawCash(String accountNumber, BigDecimal amount);

    ApiResponse<TransferResponse> transferAmount(String fromAccount, String toAccount, BigDecimal amount);

    ApiResponse<BalanceDTO> getBalance(String accountNumber);
}
