package com.bank.DigitalBank.Service.Impl;


import com.bank.DigitalBank.Controller.BankController;
import com.bank.DigitalBank.DTO.TransferResponse;
import com.bank.DigitalBank.Entity.Account;
import com.bank.DigitalBank.Entity.Transaction;
import com.bank.DigitalBank.Entity.User;
import com.bank.DigitalBank.Entity.enums.TransactionType;
import com.bank.DigitalBank.Repository.AccountRepo;
import com.bank.DigitalBank.Repository.TransactionRepo;
import com.bank.DigitalBank.Repository.UserRepo;
import com.bank.DigitalBank.Service.AccountService;
import com.bank.DigitalBank.dto.ApiResponse;
import com.bank.DigitalBank.dto.BalanceDTO;
import com.bank.DigitalBank.dto.DepositResponseDTO;
import com.bank.DigitalBank.dto.WithdrawResponseDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class AccountServiceImpl implements AccountService {

    private AccountRepo accountRepo;
    private UserRepo userRepo;

    private TransactionRepo transactionRepo;

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);
    public AccountServiceImpl(AccountRepo accountRepo, UserRepo userRepo, TransactionRepo transactionRepo) {
        this.accountRepo = accountRepo;
        this.userRepo = userRepo;
        this.transactionRepo = transactionRepo;
    }

    @Override
    public ApiResponse<Account> register(Account account) {

        Long userId = account.getUser().getId();
        User fullUser = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        account.setUser(fullUser); // Set fully hydrated user
        Account savedAccount = accountRepo.save(account);
        logger.info("Bank Account Created : "+savedAccount.getAccountNumber());
ApiResponse accountresponse = new ApiResponse<>(true,"Account registered successfully",savedAccount);
        return accountresponse;
    }

    @Transactional
    @Override
    public ApiResponse<DepositResponseDTO> depositCash(String accountNumber, BigDecimal amount) {

        Account account = accountRepo.findByAccountNumber(accountNumber);
        BigDecimal newBalance = account.getBalance().add(amount);

        account.setBalance(newBalance);

        accountRepo.save(account);

        Transaction transaction = new Transaction(
                null,                         // id (auto-generated)
                null,                         // fromAccount
                account.getAccountNumber(),   // toAccount
                TransactionType.DEPOSIT,      // transactionType
                amount,                       // amount
                null                          // transactionDate (auto-set by @CreationTimestamp)
        );
        transactionRepo.save(transaction);

        DepositResponseDTO saved = new DepositResponseDTO(
                "SUCCESS",
                "Deposit successful",
                account.getAccountNumber(),
                account.getBalance()
        );

        logger.info("Money Deposited to : "+saved.getAccountNumber() + "with balance "+ saved.getNewBalance());
        return new ApiResponse<>(true,"Transaction was successfull",saved);
    }


    @Transactional
    @Override
    public ApiResponse<WithdrawResponseDTO> withdrawCash(String accountNumber, BigDecimal amount) {
        Account account = accountRepo.findByAccountNumber(accountNumber);
        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);

        accountRepo.save(account);

        Transaction transaction = new Transaction(
                null,                         // id (auto-generated)
                null,                         // fromAccount
                account.getAccountNumber(),   // toAccount
                TransactionType.WITHDRAWAL,      // transactionType
                amount,                       // amount
                null                          // transactionDate (auto-set by @CreationTimestamp)
        );
        transactionRepo.save(transaction);

        WithdrawResponseDTO withdrawResponseDTO = new WithdrawResponseDTO(
                "SUCCESS",
                "Withdraw successful",
                account.getAccountNumber(),
                account.getBalance());

        logger.info("Money Withdrawn from : "+withdrawResponseDTO.getAccountNumber() +  "with new balance "+ withdrawResponseDTO.getNewBalance());
        return new ApiResponse<>(true,"Withdrawal was successfull",withdrawResponseDTO);

    }

    @Override
    public ApiResponse<TransferResponse> transferAmount(String fromAccount, String toAccount, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        }

        Account fromAccountt = accountRepo.findByAccountNumber(fromAccount);
        Account toAccountt = accountRepo.findByAccountNumber(toAccount);

        if (fromAccount == null || toAccount == null) {
            throw new IllegalArgumentException("Invalid account number");
        }

        if (fromAccountt.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance in source account");
        }

        // Perform transfer
        fromAccountt.setBalance(fromAccountt.getBalance().subtract(amount));
        toAccountt.setBalance(toAccountt.getBalance().add(amount));

        accountRepo.save(fromAccountt);
        accountRepo.save(toAccountt);

        // Save transaction (debit)
        Transaction t = transactionRepo.save(new Transaction(
                null,
                fromAccountt.getAccountNumber(),
                toAccountt.getAccountNumber(),
                TransactionType.TRANSFER,
                amount,
                LocalDateTime.now()
        ));
TransferResponse transferResponse = new TransferResponse(fromAccount, toAccount, amount);

        logger.info("Money Deposited to : "+transferResponse.getToAccount() + "from "+transferResponse.getFromAccount() +"with balance "+t.getAmount());
        return new ApiResponse<>(true,"Transfer was successfull",transferResponse);
    }

    @Override
    public ApiResponse<BalanceDTO> getBalance(String accountNumber) {
        Account account = accountRepo.findByAccountNumber(accountNumber);

        BalanceDTO savedbalance = new BalanceDTO();
        savedbalance.setBalance(account.getBalance());
        savedbalance.setAccountNumber(account.getAccountNumber());

        logger.info("Balance for "+accountNumber +"is " + savedbalance.getBalance());

        return new ApiResponse<>(true,"balance is"+account.getBalance(),savedbalance);
    }
}
