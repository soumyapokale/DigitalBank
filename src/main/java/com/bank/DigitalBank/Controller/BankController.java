package com.bank.DigitalBank.Controller;


import com.bank.DigitalBank.Entity.Account;
import com.bank.DigitalBank.Entity.User;
import com.bank.DigitalBank.Service.AccountService;
import com.bank.DigitalBank.Service.UserService;
import com.bank.DigitalBank.dto.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BankController {

    private UserService userService;

    private AccountService accountService;

    private static final Logger logger = LoggerFactory.getLogger(BankController.class);
    public BankController(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    @PostMapping(value="/users/register",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<UserDto>> registerUser( @Valid @RequestBody UserDto user){
        logger.info("Registering User in Bank of Soumya");
        ApiResponse<UserDto> saveduser = userService.register(user);
        logger.info("Registered User in Bank of Soumya successfully");
        return new ResponseEntity<>(saveduser,HttpStatus.CREATED);
    }

    @PostMapping("/accounts/create")
    public ResponseEntity<ApiResponse<AccountDto>> registerUserAccount(@Valid  @RequestBody AccountDto account){
        logger.info("Registering User Bank Account in Bank of Soumya");
        ApiResponse<AccountDto> savedAccount = accountService.register(account);
        logger.info("Registered User Bank Account in Bank of Soumya successfully");
        return new ResponseEntity<>(savedAccount,HttpStatus.CREATED);
    }

    @PostMapping("/accounts/deposit")
    public ResponseEntity<ApiResponse<DepositResponseDTO>> depositsCash(@RequestBody @Valid DepositRequestDTO request){
        logger.info("Depositing Money");
        ApiResponse<DepositResponseDTO> savedAccount = accountService.depositCash(request.getAccountNumber(),request.getAmount());
        logger.info("Deposited Money in " + request.getAccountNumber());
        return new ResponseEntity<>(savedAccount,HttpStatus.OK);
    }

    @PostMapping("/accounts/withdraw")
    public ResponseEntity<ApiResponse<WithdrawResponseDTO>> withdrawCash(@RequestBody @Valid WithdrawRequestDTO request){
        logger.info("Withdrawing Money");
        ApiResponse<WithdrawResponseDTO> savedAccount = accountService.withdrawCash(request.getAccountNumber(),request.getAmount());
        logger.info("Withdrawed Money from bank");
        return new ResponseEntity<>(savedAccount,HttpStatus.OK);
    }

    @PostMapping("/accounts/transfer")
    public ResponseEntity<ApiResponse<com.bank.DigitalBank.dto.TransferResponse>> transfer(@RequestBody @Valid TransferRequest request) {
        logger.info("Transfering");
        ApiResponse<com.bank.DigitalBank.dto.TransferResponse> transferResponse = accountService.transferAmount(
                request.getFromAccount(),
                request.getToAccount(),
                request.getAmount()
        );

        logger.info("Transferred from" + request.getFromAccount() +"to "+request.getToAccount()+"with amount "+ request.getAmount());

        return new ResponseEntity<>(transferResponse, HttpStatus.OK);
    }

    @GetMapping("/accounts/balance/{accountNumber}")
    public ResponseEntity<ApiResponse<BalanceDTO>> getBalance(@PathVariable String accountNumber){
        logger.info("Fetching Balance");
        ApiResponse<BalanceDTO> account = accountService.getBalance(accountNumber);
        logger.info("Balance is "+ account.getData().getBalance());
        return new ResponseEntity<>(account,HttpStatus.OK);
    }

    @PostMapping("users/login")

    public ResponseEntity<ApiResponse<LoginResponse>> loginUser(@RequestBody @Valid LoginRequest request) throws Exception {
        logger.info("trying to login");
        ApiResponse<LoginResponse> response = userService.login(request);
        logger.info("Login Success");
        return new ResponseEntity<>(response,HttpStatus.OK);

    }

    @GetMapping("users/transaction/{accountNumber}")
    public ResponseEntity<ApiResponse<List<TrasactionResponse>>> getAccountTransactionHistory(@Valid @PathVariable String accountNumber){
        ApiResponse<List<TrasactionResponse>> response = accountService.getTransactionHistory(accountNumber);
        logger.info("Fetching transaction history");
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("users/account/{accountNumber}/summary")
    public ResponseEntity<ApiResponse<AccountSummaryDTO>> showAccountSummary(@Valid @PathVariable String accountNumber){
        ApiResponse<AccountSummaryDTO> accountSummary = accountService.getAccountSummary(accountNumber);
        logger.info("Fetching Account Summary");
        return new ResponseEntity<>(accountSummary,HttpStatus.OK);

    }

    @GetMapping("/accounts/{accountNumber}/mini-statement")
    public ResponseEntity<ApiResponse<StatementResponse>> getMiniStatement(@Valid @PathVariable String accountNumber){
        ApiResponse<StatementResponse> response = accountService.getStatement(accountNumber);

        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("admin/dashboard")

    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getAdminDashboard(){

        ApiResponse<AdminDashboardResponse> response = accountService.getAdminDashboard();

        return new ResponseEntity<>(response,HttpStatus.OK);

    }


    @GetMapping("/accounts/{accountNumber}/transactions/export")
    public void transactionCSV(@PathVariable String accountNumber,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                               HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"transactions_" + accountNumber + ".csv\"");
        accountService.toTransactionCSV(accountNumber, fromDate, toDate, response.getWriter());
    }




}
