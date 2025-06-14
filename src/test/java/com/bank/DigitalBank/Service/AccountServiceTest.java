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
import com.bank.DigitalBank.Utils.JsonUtil;
import com.bank.DigitalBank.dto.*;
import org.apiguardian.api.API;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private AccountRepo accountRepo;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private TransactionRepo transactionRepo;

    private AccountDto validAccount;

    private Account realAccount;

    private  UserDto validUser;

    private Transaction testTransaction;

    @Mock
    private UserRepo userRepo;
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
                user
        );

    }

    @Test
    public void Register(){

        User user = new User();
        user.setId(validAccount.getUserId());
        realAccount = new Account(validAccount.getId(), validAccount.getAccountNumber(), validAccount.getBalance(),validAccount.getCreatedAt(),user);

        when(userRepo.findById(validAccount.getUserId())).thenReturn(Optional.of(user));


        when(accountMapper.toAccount(validAccount)).thenReturn(realAccount);

        when(accountRepo.save(any(Account.class))).thenReturn(realAccount);

        when(accountMapper.toAccountDto(realAccount)).thenReturn(validAccount);

        when(transactionRepo.save(any(Transaction.class))).thenReturn(testTransaction);

        ApiResponse<AccountDto> response = accountService.register(validAccount);

        assertTrue(response.isSuccess());

        assertEquals(response.getData().getAccountNumber(),validAccount.getAccountNumber());

        assertEquals(response.getData().getBalance(),validAccount.getBalance());

    }

    @Test
    public void testDepositCash(){


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

        ApiResponse<DepositResponseDTO>  response= accountService.depositCash(realAccount.getAccountNumber(),realAccount.getBalance());

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
        Account fromAccount = new Account(1L, fromAccountNumber, new BigDecimal("1000.00"), LocalDateTime.now(), user);
        Account toAccount = new Account(2L, toAccountNumber, new BigDecimal("200.00"), LocalDateTime.now(), user);

        // Updated balances
        BigDecimal fromNewBalance = fromAccount.getBalance().subtract(transferAmount);
        BigDecimal toNewBalance = toAccount.getBalance().add(transferAmount);

        // Set expectations
        when(accountRepo.findByAccountNumber(fromAccountNumber)).thenReturn(fromAccount);
        when(accountRepo.findByAccountNumber(toAccountNumber)).thenReturn(toAccount);
        when(accountRepo.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction testTransaction = new Transaction(
                null,
                fromAccountNumber,
                toAccountNumber,
                TransactionType.TRANSFER,
                transferAmount,
                LocalDateTime.now()
        );

        when(transactionRepo.save(any(Transaction.class))).thenReturn(testTransaction);

        // Act
        ApiResponse<TransferResponse> response = accountService.transferAmount(fromAccountNumber, toAccountNumber, transferAmount);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("Transfer was successfull", response.getMessage());

        TransferResponse data = response.getData();
        assertEquals(fromAccountNumber, data.getFromAccount());
        assertEquals(toAccountNumber, data.getToAccount());
        assertEquals(transferAmount, data.getTransferredAmount());

        // Confirm balances changed in mock objects
        assertEquals(fromNewBalance, fromAccount.getBalance());
        assertEquals(toNewBalance, toAccount.getBalance());
    }

    @Test

    public void getBalance(){
        when(accountRepo.findByAccountNumber(anyString())).thenReturn(realAccount);

        BalanceDTO savedbalance = new BalanceDTO();
        savedbalance.setBalance(realAccount.getBalance());
        savedbalance.setAccountNumber(realAccount.getAccountNumber());

        ApiResponse<BalanceDTO> response= accountService.getBalance(realAccount.getAccountNumber());

        assertTrue(response.isSuccess());
        assertEquals(response.getData().getBalance(),savedbalance.getBalance());
        assertEquals(response.getData().getAccountNumber(),savedbalance.getAccountNumber());

    }


}