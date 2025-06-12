package com.bank.DigitalBank.Service.Impl;


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

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class AccountServiceImpl implements AccountService {

    private AccountRepo accountRepo;
    private UserRepo userRepo;

    private TransactionRepo transactionRepo;

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
        return new ApiResponse<>(true,"Transaction was successfull",saved);
    }


    @Transactional
    @Override
    public WithdrawResponseDTO withdrawCash(String accountNumber, BigDecimal amount) {
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
        return new WithdrawResponseDTO(
                "SUCCESS",
                "Deposit successful",
                account.getAccountNumber(),
                account.getBalance());

    }

    @Override
    public TransferResponse transferAmount(String fromAccount, String toAccount, BigDecimal amount) {
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
        transactionRepo.save(new Transaction(
                null,
                fromAccountt.getAccountNumber(),
                toAccountt.getAccountNumber(),
                TransactionType.TRANSFER,
                amount,
                LocalDateTime.now()
        ));

        return new TransferResponse(fromAccount, toAccount, amount);
    }

    @Override
    public BalanceDTO getBalance(String accountNumber) {
        Account account = accountRepo.findByAccountNumber(accountNumber);

        BalanceDTO savedbalance = new BalanceDTO();
        savedbalance.setBalance(account.getBalance());
        savedbalance.setAccountNumber(account.getAccountNumber());



        return savedbalance;
    }
}
