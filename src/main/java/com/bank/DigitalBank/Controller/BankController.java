package com.bank.DigitalBank.Controller;


import com.bank.DigitalBank.Entity.Account;
import com.bank.DigitalBank.Entity.User;
import com.bank.DigitalBank.Service.AccountService;
import com.bank.DigitalBank.Service.UserService;
import com.bank.DigitalBank.dto.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ApiResponse<User>> registerUser(@RequestBody UserDto user){
        logger.info("Registering User in Bank of Soumya");
        ApiResponse<User> saveduser = userService.register(user);
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
    public ResponseEntity<ApiResponse<DepositResponseDTO>> depositsCash(@RequestBody DepositRequestDTO request){
        logger.info("Depositing Money");
        ApiResponse<DepositResponseDTO> savedAccount = accountService.depositCash(request.getAccountNumber(),request.getAmount());
        logger.info("Deposited Money in " + request.getAccountNumber());
        return new ResponseEntity<>(savedAccount,HttpStatus.OK);
    }

    @PostMapping("/accounts/withdraw")
    public ResponseEntity<ApiResponse<WithdrawResponseDTO>> depositsCash(@RequestBody WithdrawRequestDTO request){
        logger.info("Withdrawing Money");
        ApiResponse<WithdrawResponseDTO> savedAccount = accountService.withdrawCash(request.getAccountNumber(),request.getAmount());
        logger.info("Withdrawed Money from bank");
        return new ResponseEntity<>(savedAccount,HttpStatus.OK);
    }

    @PostMapping("/accounts/transfer")
    public ResponseEntity<ApiResponse<com.bank.DigitalBank.DTO.TransferResponse>> transfer(@RequestBody TransferRequest request) {
        logger.info("Transfering");
        ApiResponse<com.bank.DigitalBank.DTO.TransferResponse> transferResponse = accountService.transferAmount(
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

}
