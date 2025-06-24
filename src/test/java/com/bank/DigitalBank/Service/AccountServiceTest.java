package com.bank.DigitalBank.Service;

import com.bank.DigitalBank.Entity.Account;
import com.bank.DigitalBank.Entity.Transaction;
import com.bank.DigitalBank.Entity.User;
import com.bank.DigitalBank.Entity.enums.TransactionType;
import com.bank.DigitalBank.Mapper.AccountMapper;
import com.bank.DigitalBank.Repository.AccountRepo;
import com.bank.DigitalBank.Repository.TransactionRepo;
import com.bank.DigitalBank.Repository.UserRepo;
import com.bank.DigitalBank.Service.Impl.AccountServiceImpl;
import com.bank.DigitalBank.Utils.GenerateDiscription;
import com.bank.DigitalBank.Utils.JsonUtil;
import com.bank.DigitalBank.dto.*;
import jakarta.validation.constraints.AssertTrue;
import org.apiguardian.api.API;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @InjectMocks
    private AccountServiceImpl accountService;
    private String testAccountNumber = "ACC123";

    @Mock
    private AccountRepo accountRepo;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private TransactionRepo transactionRepo;

    private AccountDto validAccount;

    private Account realAccount;

    private UserDto validUser;

    @Mock
    private EmailService emailService;

    private Transaction testTransaction;

    @Mock
    private UserRepo userRepo;


    @Mock
    private GenerateDiscription generateDiscription;

    @BeforeEach
    void setup() throws IOException {


        validUser = JsonUtil.readJsonFileAsObject("ValidUser.json", UserDto.class);
        validAccount = JsonUtil.readJsonFileAsObject("ValidAccount.json", AccountDto.class);
        testTransaction = new Transaction(
                null,                         // id
                null,                         // fromAccount
                "1234567890",                 // toAccount
                TransactionType.DEPOSIT,      // transactionType
                new BigDecimal("1000.00"),   // amount
                null                          // transactionDate
        );
        User user = new User();
        user.setId(validAccount.getUserId());
        realAccount = new Account(
                validAccount.getId(),
                validAccount.getAccountNumber(),
                validAccount.getBalance(),
                validAccount.getCreatedAt(),
                user,new BigDecimal(3.5)
        );

    }

    @Test
    public void Register() {

        User user = new User();
        user.setId(validAccount.getUserId());
        realAccount = new Account(validAccount.getId(), validAccount.getAccountNumber(), validAccount.getBalance(), validAccount.getCreatedAt(), user,new BigDecimal(3.5));

        when(userRepo.findById(validAccount.getUserId())).thenReturn(Optional.of(user));


        when(accountMapper.toAccount(validAccount)).thenReturn(realAccount);

        when(accountRepo.save(any(Account.class))).thenReturn(realAccount);

        when(accountMapper.toAccountDto(realAccount)).thenReturn(validAccount);

        when(transactionRepo.save(any(Transaction.class))).thenReturn(testTransaction);

        ApiResponse<AccountDto> response = accountService.register(validAccount);

        assertTrue(response.isSuccess());

        assertEquals(response.getData().getAccountNumber(), validAccount.getAccountNumber());

        assertEquals(response.getData().getBalance(), validAccount.getBalance());

    }

    @Test
    public void testDepositCash() {


        BigDecimal newBalance = new BigDecimal(10000);

        realAccount.setBalance(newBalance);
        when(accountRepo.findByAccountNumber(realAccount.getAccountNumber())).thenReturn(realAccount);

        when(accountRepo.save(any(Account.class))).thenReturn(realAccount);
        when(transactionRepo.save(any(Transaction.class))).thenReturn(testTransaction);
        DepositResponseDTO saved = new DepositResponseDTO(
                "SUCCESS",
                "Deposit successful",
                realAccount.getAccountNumber(),
                realAccount.getBalance()
        );

        ApiResponse<DepositResponseDTO> response = accountService.depositCash(realAccount.getAccountNumber(), realAccount.getBalance());

        assertTrue(response.isSuccess());
        assertEquals(validAccount.getAccountNumber(), response.getData().getAccountNumber());
        assertEquals(realAccount.getBalance(), response.getData().getNewBalance());

    }

    @Test
    public void withdrawMoney() {
        BigDecimal currentBalance = new BigDecimal("1000.00");
        BigDecimal withdrawAmount = new BigDecimal("100.00");
        BigDecimal expectedBalance = currentBalance.subtract(withdrawAmount);

        realAccount.setBalance(currentBalance);

        when(accountRepo.findByAccountNumber(realAccount.getAccountNumber())).thenReturn(realAccount);
        when(accountRepo.save(any(Account.class))).thenReturn(realAccount);
        when(transactionRepo.save(any(Transaction.class))).thenReturn(testTransaction);

        ApiResponse<WithdrawResponseDTO> response = accountService.withdrawCash(
                realAccount.getAccountNumber(),
                withdrawAmount
        );

        assertTrue(response.isSuccess());
        assertEquals(realAccount.getAccountNumber(), response.getData().getAccountNumber());
        assertEquals(expectedBalance, response.getData().getNewBalance());
    }

    @Test
    public void testTransferAmount_success() {
        // Arrange
        String fromAccountNumber = "SBI11111111";
        String toAccountNumber = "SBI22222222";
        BigDecimal transferAmount = new BigDecimal("500.00");

        // Create mock User
        User user = new User();
        user.setId(1L);

        // Setup accounts
        Account fromAccount = new Account(1L, fromAccountNumber, new BigDecimal("1000.00"), LocalDateTime.now(), user,new BigDecimal(3.5));
        Account toAccount = new Account(2L, toAccountNumber, new BigDecimal("200.00"), LocalDateTime.now(), user,new BigDecimal(3.5));

        // Updated balances
        BigDecimal fromNewBalance = fromAccount.getBalance().subtract(transferAmount);
        BigDecimal toNewBalance = toAccount.getBalance().add(transferAmount);

        // Set expectations
        when(accountRepo.findByAccountNumber(fromAccountNumber)).thenReturn(fromAccount);
        when(accountRepo.findByAccountNumber(toAccountNumber)).thenReturn(toAccount);
        when(accountRepo.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction testDebit = new Transaction(
                null,
                fromAccountNumber,
                toAccountNumber,
                TransactionType.DEBIT,
                transferAmount,
                LocalDateTime.now()
        );
        Transaction testCredit = new Transaction(
                null,
                fromAccountNumber,
                toAccountNumber,
                TransactionType.CREDIT,
                transferAmount,
                LocalDateTime.now()
        );


        // Save just returns the argument passed

        when(transactionRepo.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Let the email stubbing be lenient to avoid strict stubbing errors
        lenient().doNothing().when(emailService)
                .sendEmail(nullable(String.class), nullable(String.class), nullable(String.class));




        // Act
        ApiResponse<TransferResponse> response = accountService.transferAmount(fromAccountNumber, toAccountNumber, transferAmount);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("Transferred "+transferAmount+" from "+fromAccountNumber+" to "+toAccountNumber, response.getMessage());

        TransferResponse data = response.getData();
        assertEquals(fromAccountNumber, data.getFromAccount());
        assertEquals(toAccountNumber, data.getToAccount());
        assertEquals(transferAmount, data.getTransferredAmount());

        // Confirm balances changed in mock objects
        assertEquals(fromNewBalance, fromAccount.getBalance());
        assertEquals(toNewBalance, toAccount.getBalance());
    }

    @Test

    public void getBalance() {
        when(accountRepo.findByAccountNumber(anyString())).thenReturn(realAccount);

        BalanceDTO savedbalance = new BalanceDTO();
        savedbalance.setBalance(realAccount.getBalance());
        savedbalance.setAccountNumber(realAccount.getAccountNumber());

        ApiResponse<BalanceDTO> response = accountService.getBalance(realAccount.getAccountNumber());

        assertTrue(response.isSuccess());
        assertEquals(response.getData().getBalance(), savedbalance.getBalance());
        assertEquals(response.getData().getAccountNumber(), savedbalance.getAccountNumber());

    }

    // ❌ Error scenarios

    @Test
    public void register_shouldFailWhenUserNotFound() {
        when(userRepo.findById(validAccount.getUserId())).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> accountService.register(validAccount));
        assertEquals("User not found with id: " + validAccount.getUserId(), ex.getMessage());
    }

    @Test
    public void deposit_shouldFailWhenAccountNotFound() {
        when(accountRepo.findByAccountNumber(anyString())).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                accountService.depositCash("INVALID_ACC", BigDecimal.valueOf(1000)));

        assertEquals("Account not found with account number: INVALID_ACC", ex.getMessage());
    }



    @Test
    public void transfer_shouldFailWhenAccountsInvalid() {
        when(accountRepo.findByAccountNumber("FROM_ACC")).thenReturn(null);
        when(accountRepo.findByAccountNumber("TO_ACC")).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                accountService.transferAmount("FROM_ACC", "TO_ACC", BigDecimal.valueOf(100)));

        assertEquals("Invalid account number", ex.getMessage());
    }

    @Test
    public void transfer_shouldFailWhenInsufficientFunds() {
        Account from = new Account(1L, "FROM", new BigDecimal("50.00"), LocalDateTime.now(), new User(),new BigDecimal(3.5));
        Account to = new Account(2L, "TO", new BigDecimal("200.00"), LocalDateTime.now(), new User(),new BigDecimal(3.5));

        when(accountRepo.findByAccountNumber("FROM")).thenReturn(from);
        when(accountRepo.findByAccountNumber("TO")).thenReturn(to);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                accountService.transferAmount("FROM", "TO", new BigDecimal("100.00")));

        assertEquals("Insufficient balance in source account", ex.getMessage());
    }

    public static List<Transaction> getTransactionListtoAccount() {
        return Arrays.asList(
                new Transaction(1L, "ACC123", "SBI123456789", TransactionType.CREDIT, new BigDecimal("1500.00"), LocalDateTime.now().minusDays(1)),
                new Transaction(2L, null, "SBI123456789", TransactionType.DEPOSIT, new BigDecimal("500.00"), LocalDateTime.now().minusDays(2)),
                new Transaction(3L, "ACC789", "null", TransactionType.WITHDRAWAL, new BigDecimal("300.00"), LocalDateTime.now().minusDays(3)),
                new Transaction(4L, "ACC456", "SBI123456789", TransactionType.DEBIT, new BigDecimal("750.00"), LocalDateTime.now())
        );
    }

    @Test
    public void transactionHistoryTest() {
        String accountNumber = "SBI123456789";

        List<Transaction> toTransactions = Arrays.asList(
                new Transaction(1L, "ACC123", "SBI123456789", TransactionType.DEBIT, new BigDecimal("1500.00"), LocalDateTime.now().minusDays(1)),
                new Transaction(2L, null, "SBI123456789", TransactionType.DEPOSIT, new BigDecimal("500.00"), LocalDateTime.now().minusDays(2)),
                new Transaction(4L, "ACC456", "SBI123456789", TransactionType.CREDIT, new BigDecimal("750.00"), LocalDateTime.now())
        );

        List<Transaction> fromTransactions = Arrays.asList(
                new Transaction(3L, "SBI123456789", "ACC999", TransactionType.WITHDRAWAL, new BigDecimal("300.00"), LocalDateTime.now().minusDays(3))
        );

        when(transactionRepo.findByToAccount(accountNumber)).thenReturn(toTransactions);
        when(transactionRepo.findByFromAccount(accountNumber)).thenReturn(fromTransactions);

        ApiResponse<List<TrasactionResponse>> response = accountService.getTransactionHistory(accountNumber);

        assertTrue(response.isSuccess());
        assertEquals(4, response.getData().size());

        TrasactionResponse dto = response.getData().get(0);
        assertNotNull(dto.getTransactionType());
        assertNotNull(dto.getAmount());
    }

//
    @Test
    public void accountSummary() {
        // Given
        String accountNumber = "SBI123456789";

        Transaction t1 = new Transaction(
                1L,
                null,
                accountNumber,
                TransactionType.DEPOSIT,
                new BigDecimal("1000.00"),
                LocalDateTime.now().minusDays(1)
        );

        Transaction t2 = new Transaction(
                2L,
                accountNumber,
                null,
                TransactionType.WITHDRAWAL,
                new BigDecimal("1000.00"),
                LocalDateTime.now().minusDays(1)
        );

        Transaction t3 = new Transaction(
                3L,
                "ACC111111111",
                accountNumber,
                TransactionType.CREDIT,
                new BigDecimal("750.00"),
                LocalDateTime.now()
        );

        Transaction t4 = new Transaction(
                3L,
                "ACC111111111",
                accountNumber,
                TransactionType.DEBIT,
                new BigDecimal("750.00"),
                LocalDateTime.now()
        );

        User testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("securePassword");
        testUser.setRoles("USER");

        Account testAccount = new Account();
        testAccount.setId(101L);
        testAccount.setAccountNumber(accountNumber);
        testAccount.setBalance(new BigDecimal("5000.00"));
        testAccount.setCreatedAt(LocalDateTime.now().minusMonths(1));
        testAccount.setUser(testUser);

        List<Transaction> deposits = List.of(t1);
        List<Transaction> withdrawals = List.of(t2);
        List<Transaction> credit = List.of(t3);
        List<Transaction> debit = List.of(t4);

        when(transactionRepo.findByToAccountAndType(accountNumber, TransactionType.DEPOSIT)).thenReturn(deposits);
        when(transactionRepo.findByToAccountAndType(accountNumber, TransactionType.WITHDRAWAL)).thenReturn(withdrawals);
        when(transactionRepo.findByToAccountAndType(accountNumber, TransactionType.CREDIT)).thenReturn(credit);
        when(transactionRepo.findByToAccountAndType(accountNumber, TransactionType.DEBIT)).thenReturn(debit);
        when(accountRepo.findByAccountNumber(accountNumber)).thenReturn(testAccount);
        when(userRepo.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        // When
        ApiResponse<AccountSummaryDTO> response = accountService.getAccountSummary(accountNumber);

        // Then
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());

        AccountSummaryDTO dto = response.getData();
        assertEquals(accountNumber, dto.getAccountNumber());
        assertEquals("Test User", dto.getHolderName());
        assertEquals(new BigDecimal("5000.00"), dto.getBalance());
        assertEquals(new BigDecimal("1000.00"), dto.getTotalDeposits());
        assertEquals(new BigDecimal("1000.00"), dto.getTotalWithdrawals());
        assertEquals(new BigDecimal("750.00"), dto.getTotalCredit());
        assertEquals(new BigDecimal("750.00"), dto.getTotalDebit());
    }


    @Test
    public void TestStatement() {
        String accountNumber = "SKI101010023";
        User user = new User();
        user.setId(1L);

        Account account = new Account(1L, accountNumber, new BigDecimal("2000.00"), LocalDateTime.now(), user,new BigDecimal(3.5));

        when(accountRepo.findByAccountNumber(accountNumber)).thenReturn(account);
        when(transactionRepo.findTop5ByFromAccountOrToAccountOrderByTransactionDateDesc(accountNumber, accountNumber))
                .thenReturn(getTop5Transactions(accountNumber));

        try (MockedStatic<GenerateDiscription> mockedStatic = mockStatic(GenerateDiscription.class)) {
            mockedStatic.when(() -> GenerateDiscription.generateDescription(any(Transaction.class), eq(accountNumber)))
                    .thenReturn("description generated");}

        ApiResponse<StatementResponse> response = accountService.getStatement(accountNumber);

        assertTrue(response.isSuccess());
        List<MiniStatementResponse> miniStatements = response.getData().getMiniStatementResponse();
        assertEquals(5, miniStatements.size());

        assertEquals(new BigDecimal("2000.00"), miniStatements.get(0).getBalanceAfterTransaction()); // DEBIT 100
        assertEquals(new BigDecimal("2100.00"), miniStatements.get(1).getBalanceAfterTransaction()); // CREDIT 200
        assertEquals(new BigDecimal("1900.00"), miniStatements.get(2).getBalanceAfterTransaction()); // DEBIT 150
        assertEquals(new BigDecimal("2050.00"), miniStatements.get(3).getBalanceAfterTransaction()); // DEPOSIT 500
        assertEquals(new BigDecimal("1550.00"), miniStatements.get(4).getBalanceAfterTransaction()); // WITHDRAWAL 300

    }

    @Test
    public void getStatement_shouldFailWhenAccountNotFound() {
        when(accountRepo.findByAccountNumber(testAccountNumber)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                accountService.getStatement(testAccountNumber));

        assertEquals("Account not found: " + testAccountNumber, ex.getMessage());
    }

    @Test
    public void getTransactionHistory_shouldReturnEmptyWhenNoTransactions() {
        when(transactionRepo.findByToAccount(testAccountNumber)).thenReturn(Collections.emptyList());
        when(transactionRepo.findByFromAccount(testAccountNumber)).thenReturn(Collections.emptyList());

        ApiResponse<List<TrasactionResponse>> response = accountService.getTransactionHistory(testAccountNumber);

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals(0, response.getData().size());
    }

    @Test
    public void getBalance_shouldFailWhenAccountNotFound() {
        when(accountRepo.findByAccountNumber(anyString())).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                accountService.getBalance("NOTFOUND"));

        assertEquals("Account not found with number: NOTFOUND", ex.getMessage());
    }

    @Test
    public void withdraw_shouldFailWhenAccountNotFound() {
        when(accountRepo.findByAccountNumber(anyString())).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                accountService.withdrawCash("INVALID_ACC", BigDecimal.valueOf(100)));

        assertEquals("Account not found with number: INVALID_ACC", ex.getMessage());
    }

    @Test
    public void transfer_shouldFailWhenAmountIsZeroOrNegative() {
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () ->
                accountService.transferAmount("FROM", "TO", BigDecimal.ZERO));

        assertEquals("Transfer amount must be greater than zero", ex1.getMessage());

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () ->
                accountService.transferAmount("FROM", "TO", new BigDecimal("-100")));

        assertEquals("Transfer amount must be greater than zero", ex2.getMessage());
    }



    @Test
    public void accountSummary_shouldFailWhenUserNotFound() {
        User user = new User();
        user.setId(101L);
        Account acc = new Account(1L, testAccountNumber, BigDecimal.valueOf(1000), LocalDateTime.now(), user,new BigDecimal(3.5));

        when(accountRepo.findByAccountNumber(testAccountNumber)).thenReturn(acc);
        when(userRepo.findById(101L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                accountService.getAccountSummary(testAccountNumber));

        assertEquals("User is not valid", ex.getMessage());
    }

    @Test
    public void transfer_shouldFailWhenAccountsAreInvalid() {
        when(accountRepo.findByAccountNumber("FROM_ACC")).thenReturn(null);
        when(accountRepo.findByAccountNumber("TO_ACC")).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                accountService.transferAmount("FROM_ACC", "TO_ACC", BigDecimal.valueOf(100)));

        assertEquals("Invalid account number", ex.getMessage());
    }

    @Test
    public void transfer_shouldFailWhenInsufficientBalance() {
        Account from = new Account(1L, "FROM", new BigDecimal("50.00"), LocalDateTime.now(), new User(),new BigDecimal(3.5));
        Account to = new Account(2L, "TO", new BigDecimal("200.00"), LocalDateTime.now(), new User(),new BigDecimal(3.5));

        when(accountRepo.findByAccountNumber("FROM")).thenReturn(from);
        when(accountRepo.findByAccountNumber("TO")).thenReturn(to);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                accountService.transferAmount("FROM", "TO", new BigDecimal("100.00")));

        assertEquals("Insufficient balance in source account", ex.getMessage());
    }

    @Test
    public void withdraw_shouldFailWhenInsufficientBalance() {
        Account acc = new Account(1L, testAccountNumber, BigDecimal.valueOf(100), LocalDateTime.now(), new User(),new BigDecimal(3.5));
        when(accountRepo.findByAccountNumber(testAccountNumber)).thenReturn(acc);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                accountService.withdrawCash(testAccountNumber, BigDecimal.valueOf(200)));

        assertEquals("Insufficient balance", ex.getMessage());
    }




    public static List<Transaction> getTop5Transactions(String accountNumber) {
        return Arrays.asList(
                new Transaction(1L, accountNumber, null, TransactionType.DEBIT, new BigDecimal("100.00"), LocalDateTime.now().minusDays(1)),
                new Transaction(2L, null, accountNumber, TransactionType.CREDIT, new BigDecimal("200.00"), LocalDateTime.now().minusDays(2)),
                new Transaction(3L, accountNumber, null, TransactionType.DEBIT, new BigDecimal("150.00"), LocalDateTime.now().minusDays(3)),
                new Transaction(4L, null, accountNumber, TransactionType.DEPOSIT, new BigDecimal("500.00"), LocalDateTime.now().minusDays(4)),
                new Transaction(5L, accountNumber, null, TransactionType.WITHDRAWAL, new BigDecimal("300.00"), LocalDateTime.now().minusDays(5))
        );
    }

}














