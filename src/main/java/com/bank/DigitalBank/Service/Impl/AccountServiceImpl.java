package com.bank.DigitalBank.Service.Impl;




import com.bank.DigitalBank.Entity.Account;
import com.bank.DigitalBank.Entity.Transaction;
import com.bank.DigitalBank.Entity.User;
import com.bank.DigitalBank.Entity.enums.TransactionType;
import com.bank.DigitalBank.Mapper.AccountMapper;
import com.bank.DigitalBank.Repository.AccountRepo;
import com.bank.DigitalBank.Repository.TransactionRepo;
import com.bank.DigitalBank.Repository.UserRepo;
import com.bank.DigitalBank.Service.AccountService;
import com.bank.DigitalBank.Service.EmailService;
import com.bank.DigitalBank.Utils.AccountNumGenerator;
import com.bank.DigitalBank.Utils.GenerateDiscription;
import com.bank.DigitalBank.config.ModelMapperConfig;
import com.bank.DigitalBank.dto.*;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Transactional
@Service
public class AccountServiceImpl implements AccountService {

    private AccountRepo accountRepo;
    private UserRepo userRepo;
    private final EmailService emailService;

    private TransactionRepo transactionRepo;


    private ModelMapperConfig mapper;
    private AccountMapper accountMapper;
    private GenerateDiscription generateDiscription;


    private static final BigDecimal INTEREST_RATE = new BigDecimal("3.5");


    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);
    public AccountServiceImpl(AccountRepo accountRepo, UserRepo userRepo, EmailService emailService, TransactionRepo transactionRepo, ModelMapperConfig mapper, AccountMapper accountMapper) {
        this.accountRepo = accountRepo;
        this.userRepo = userRepo;
        this.emailService = emailService;
        this.transactionRepo = transactionRepo;
        this.mapper = mapper;
        this.accountMapper = accountMapper;


    }

    @Override
    public ApiResponse<AccountDto> register(AccountDto account) {

        Long userId = account.getUserId();
        User fullUser = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        account.setInterestRate(INTEREST_RATE);

        Account realaccount = accountMapper.toAccount(account);
        if (realaccount.getBalance() == null) {
            realaccount.setBalance(BigDecimal.ZERO); // or account.getBalance() if that's intended
        }

        if (realaccount.getAccountNumber() == null) {
            realaccount.setAccountNumber(AccountNumGenerator.generateAccountNumberWithPrefix());
        }
        Account savedAccount = accountRepo.save(realaccount);
        logger.info("Bank Account Created : "+savedAccount.getAccountNumber());
        AccountDto accountDto = accountMapper.toAccountDto(savedAccount);

        Transaction transaction = new Transaction(
                null,                         // id (auto-generated)
                null,                         // fromAccount
                realaccount.getAccountNumber(),   // toAccount
                TransactionType.DEPOSIT,      // transactionType
                realaccount.getBalance(),                       // amount
                null,realaccount.getBalance()                         // transactionDate (auto-set by @CreationTimestamp)
        );
        transactionRepo.save(transaction);
ApiResponse accountresponse = new ApiResponse<>(true,"Account registered successfully",accountDto);
        return accountresponse;
    }

    @Transactional
    @Override
    public ApiResponse<DepositResponseDTO> depositCash(String accountNumber, BigDecimal amount) {

        Account account = accountRepo.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found with account number: " + accountNumber);
        }
        BigDecimal newBalance = account.getBalance().add(amount);



        account.setBalance(newBalance);

        accountRepo.save(account);



        Transaction transaction = new Transaction(
                null,                         // id (auto-generated)
                null,                         // fromAccount
                account.getAccountNumber(),   // toAccount
                TransactionType.DEPOSIT,      // transactionType
                amount,                       // amount
                null,newBalance                          // transactionDate (auto-set by @CreationTimestamp)
        );
        transactionRepo.save(transaction);

        DepositResponseDTO saved = new DepositResponseDTO(
                "SUCCESS",
                "Deposit successful",
                account.getAccountNumber(),
                account.getBalance()
        );

        logger.info("Money Deposited to : "+saved.getAccountNumber() + " with balance "+ saved.getNewBalance());
        return new ApiResponse<>(true,"Transaction was successfull",saved);
    }


    @Transactional
    @Override
    public ApiResponse<WithdrawResponseDTO> withdrawCash(String accountNumber, BigDecimal amount) {
        Account account = accountRepo.findByAccountNumber(accountNumber);

        if (account == null) {
            throw new IllegalArgumentException("Account not found with number: " + accountNumber);
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance");
        }
        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);

        accountRepo.save(account);

        Transaction transaction = new Transaction(
                null,                         // id (auto-generated)
                null,                         // fromAccount
                account.getAccountNumber(),   // toAccount
                TransactionType.WITHDRAWAL,      // transactionType
                amount,                       // amount
                null,newBalance                          // transactionDate (auto-set by @CreationTimestamp)
        );
        transactionRepo.save(transaction);

        WithdrawResponseDTO withdrawResponseDTO = new WithdrawResponseDTO(
                "SUCCESS",
                "Withdraw successful",
                account.getAccountNumber(),
                account.getBalance());

        logger.info("Money Withdrawn from : "+withdrawResponseDTO.getAccountNumber() +  " with new balance "+ withdrawResponseDTO.getNewBalance());
        return new ApiResponse<>(true,"Withdrawal was successfull",withdrawResponseDTO);

    }

    @Override
    @Transactional
    public ApiResponse<TransferResponse> transferAmount(String fromAccount, String toAccount, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        }


        Account fromAccountt = accountRepo.findByAccountNumber(fromAccount);

        Account toAccountt = accountRepo.findByAccountNumber(toAccount);

        if (fromAccountt == null || toAccountt == null) {
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


        Transaction forSender = transactionRepo.save(new Transaction(null,
                fromAccountt.getAccountNumber(),
                null,
                TransactionType.DEBIT,
                amount
                ,LocalDateTime.now(),fromAccountt.getBalance()));

        Transaction forReciever = transactionRepo.save(new Transaction(null,null,
                toAccountt.getAccountNumber(),
                TransactionType.CREDIT,
                amount,
                LocalDateTime.now(),toAccountt.getBalance()));
TransferResponse transferResponse = new TransferResponse(fromAccount, toAccount, amount);

        logger.info("Money Deposited to : "+transferResponse.getToAccount() + " from "+transferResponse.getFromAccount() +" with balance "+amount);
        User receiverUser = toAccountt.getUser();
        User sender = fromAccountt.getUser();
        emailService.sendEmail(receiverUser.getEmail(), "Deposit Received", "You have recieved  "+amount+" from" + sender.getEmail());
        emailService.sendEmail(sender.getEmail(),"Money is Withdrawed",amount+" is being withdrawn from your bank account");

        return new ApiResponse<>(true,"Transferred " + amount+ " from "+fromAccount +" to " + toAccount,transferResponse);
    }

    @Override
    public ApiResponse<BalanceDTO> getBalance(String accountNumber) {
        Account account = accountRepo.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found with number: " + accountNumber);
        }

        BalanceDTO savedbalance = new BalanceDTO();
        savedbalance.setBalance(account.getBalance());
        savedbalance.setAccountNumber(account.getAccountNumber());

        logger.info("Balance for "+accountNumber +"is " + savedbalance.getBalance());

        return new ApiResponse<>(true,"balance is "+account.getBalance(),savedbalance);
    }

    @Override
    public ApiResponse<List<TrasactionResponse>> getTransactionHistory(String accountNumber) {
        List<Transaction> responseListTo = transactionRepo.findByToAccount(accountNumber);

        List<Transaction> responseListFrom = transactionRepo.findByFromAccount(accountNumber);

        List<Transaction> transactionList = Stream.concat(responseListTo.stream(), responseListFrom.stream()).toList();

        List<TrasactionResponse> responseList = transactionList.stream()
                .map(dto -> new TrasactionResponse(
                        dto.getId(),
                        dto.getTransactionType(),
                        dto.getAmount(),
                        dto.getTransactionDate()
                ))
                .sorted(Comparator.comparing(TrasactionResponse::getTransactionDate).reversed()).collect(Collectors.toList());

        ApiResponse<List<TrasactionResponse>> apiresponse = new ApiResponse<>(true,"Transaction list fetched",responseList);
        return apiresponse;

    }

    @Override
    @Transactional
    public ApiResponse<AccountSummaryDTO> getAccountSummary(String accountNumber) {
        List<Transaction> depositDetails = transactionRepo.findByToAccountAndType(accountNumber,TransactionType.DEPOSIT);
        List<Transaction> withdrawDetails = transactionRepo.findByToAccountAndType(accountNumber,TransactionType.WITHDRAWAL);
        List<Transaction> debitDetails = transactionRepo.findByToAccountAndType(accountNumber,TransactionType.DEBIT);
        List<Transaction> creditDetails = transactionRepo.findByToAccountAndType(accountNumber,TransactionType.CREDIT);


        Account account = accountRepo.findByAccountNumber(accountNumber);

        BigDecimal balance = account.getBalance();
        logger.info("Balance for "+accountNumber +"is " + balance);
        Optional<User> user = userRepo.findById(account.getUser().getId());

        String Name = user.map(User::getName).orElseThrow(()-> new RuntimeException("User is not valid"));
        logger.info("Username for "+accountNumber +"is " + Name);
        BigDecimal depositTotal = depositDetails.stream().map(Transaction::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add);

        BigDecimal withdrawTotal = withdrawDetails.stream().map(Transaction::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
        BigDecimal debitTotal = debitDetails.stream().map(Transaction::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
        BigDecimal creditTotal = creditDetails.stream().map(Transaction::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add);


        AccountSummaryDTO accountSummary = new AccountSummaryDTO(accountNumber,Name,balance,depositTotal,withdrawTotal,creditTotal,debitTotal);
        ApiResponse<AccountSummaryDTO> response = new ApiResponse<>(true,"Account summary fetched",accountSummary);

        return response;
    }

    @Override
    public ApiResponse<StatementResponse> getStatement(String accountNumber) {

        Account account = accountRepo.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account not found: " + accountNumber);
        }

        // Step 1: Start from current balance
        BigDecimal runningBalance = account.getBalance();

        // Step 2: Get top 5 latest transactions (most recent first)
        List<Transaction> transactions = transactionRepo.findTop5ByFromAccountOrToAccountOrderByTransactionDateDesc(accountNumber, accountNumber);

        List<MiniStatementResponse> statementResponses = new ArrayList<>();

        for (Transaction txn : transactions) {
            // Step 3: Create statement using current balance (AFTER this txn occurred)
            MiniStatementResponse response = new MiniStatementResponse(
                    txn.getId(),
                    txn.getTransactionType(),
                    txn.getAmount(),
                    generateDiscription.generateDescription(txn, accountNumber),
                    runningBalance,
                    txn.getTransactionDate()
            );
            statementResponses.add(response);

            // Step 4: Rewind balance to BEFORE this transaction
            if (txn.getTransactionType() == TransactionType.DEPOSIT || txn.getTransactionType() == TransactionType.CREDIT) {
                runningBalance = runningBalance.subtract(txn.getAmount());
            } else if (txn.getTransactionType() == TransactionType.WITHDRAWAL || txn.getTransactionType() == TransactionType.DEBIT) {
                runningBalance = runningBalance.add(txn.getAmount());
            }
        }

        return new ApiResponse<>(
                true,
                "Mini statement for " + accountNumber,
                new StatementResponse(statementResponses)
        );
    }

    @Override
    public ApiResponse<AdminDashboardResponse> getAdminDashboard() {
        Long TotalAccounts = accountRepo.count();

        Long totalUsers = userRepo.count();

        BigDecimal totalBalance = accountRepo.getTotalBalance();

        Long totalTransactions = transactionRepo.count();

        Long totalDeposits = transactionRepo.countByTransactionType(TransactionType.DEPOSIT) + transactionRepo.countByTransactionType(TransactionType.CREDIT);

        Long totalWithdrawals = transactionRepo.countByTransactionType(TransactionType.WITHDRAWAL) + transactionRepo.countByTransactionType(TransactionType.DEBIT);

        Long totalInterestCredited = transactionRepo.countByTransactionType(TransactionType.Interest);

        AdminDashboardResponse adminDashboardResponse = new AdminDashboardResponse(TotalAccounts,totalUsers,totalBalance,totalTransactions,totalDeposits,totalWithdrawals,totalInterestCredited);
        ApiResponse<AdminDashboardResponse> response = new ApiResponse<>(true,"Fetched Admin Dashboard",adminDashboardResponse);

        return response;
    }

    @Override
    public void toTransactionCSV(String accountNumber, LocalDate fromDate, LocalDate toDate, PrintWriter writer) {
        Account account = accountRepo.findByAccountNumber(accountNumber);
        LocalDateTime startDateTime = fromDate != null ? fromDate.atStartOfDay() : LocalDate.MIN.atStartOfDay();
        LocalDateTime endDateTime = toDate != null ? toDate.atTime(LocalTime.MAX) : LocalDateTime.now();

        List<Transaction> transactions = transactionRepo.findTransactionsForAccountBetweenDates(accountNumber,startDateTime,endDateTime);

        try(CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.EXCEL.withHeader("ID", "Type", "Amount", "BalanceAfter", "Date"))) {

            for (Transaction tx : transactions) {
                csvPrinter.printRecord(
                        tx.getId(),
                        tx.getTransactionType(),
                        tx.getAmount(),
                        tx.getSavedBalanceAfterTransaction(),

                        tx.getTransactionDate().toLocalDate()  // formatted date
                );
            }
            csvPrinter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } ;
    }

    @Override
    public ApiResponse<List<TransactionDTO>> searchTransactionByTypeAndDate(String accountNumber, TransactionType type, LocalDate from, LocalDate to) {

        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.atTime(LocalTime.MAX);

        List<Transaction> transactions = transactionRepo.findTransactionsForAccountBetweenDatesOfType(accountNumber,type,start,end);

        List<TransactionDTO> transactionDTOS = transactions.stream().map(x-> mapper.modelMapper().map(x, TransactionDTO.class)).collect(Collectors.toList());

        ApiResponse<List<TransactionDTO>> response = new ApiResponse<>(true,"Fetching Transaction for type "+type+" from "+ from + " to " +to,transactionDTOS);

        return response;

    }

    @Scheduled(cron = "0 * * * * *") // every minute for testing
    public void creditDailyInterestToAllAccounts() {
        List<Account> accounts = accountRepo.findAll(); // 🔁 apply to all accounts temporarily

        for (Account account : accounts) {
            BigDecimal balance = account.getBalance();
            BigDecimal rate = account.getInterestRate(); // already set to 3.5 for now

            BigDecimal dailyInterest = balance
                    .multiply(INTEREST_RATE)
                    .divide(BigDecimal.valueOf(100 * 365), 2, RoundingMode.HALF_UP);


            if (dailyInterest.compareTo(BigDecimal.ZERO) > 0) {
                account.setBalance(balance.add(dailyInterest));

                Transaction transaction = new Transaction();
                transaction.setToAccount(account.getAccountNumber());               // Account receiving interest
                transaction.setFromAccount("BOK");                                   // No source (bank system)
                transaction.setAmount(dailyInterest);                                    // Calculated interest
                transaction.setTransactionType(TransactionType.Interest);             // CREDIT type
                transaction.setTransactionDate(LocalDateTime.now());
                transaction.setSavedBalanceAfterTransaction(balance.add(dailyInterest));
                // Current timestamp


                transactionRepo.save(transaction);
                accountRepo.save(account);
            }
        }

        logger.info("✅ Daily interest credited to all accounts");
    }




}
