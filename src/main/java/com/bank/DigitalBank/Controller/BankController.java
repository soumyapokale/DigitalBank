package com.bank.DigitalBank.Controller;


import com.bank.DigitalBank.Entity.Account;
import com.bank.DigitalBank.Entity.User;
import com.bank.DigitalBank.Service.AccountService;
import com.bank.DigitalBank.Service.UserService;
import com.bank.DigitalBank.dto.*;
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

    public BankController(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    @PostMapping(value="/users/register",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<User>> registerUser(@RequestBody User user){

        ApiResponse<User> saveduser = userService.register(user);
        return new ResponseEntity<>(saveduser,HttpStatus.CREATED);
    }

    @PostMapping("/accounts/create")
    public ResponseEntity<ApiResponse<Account>> registerUserAccount(@RequestBody Account account){
        ApiResponse<Account> savedAccount = accountService.register(account);
        return new ResponseEntity<>(savedAccount,HttpStatus.CREATED);
    }

    @PostMapping("/accounts/deposit")
    public ResponseEntity<ApiResponse<DepositResponseDTO>> depositsCash(@RequestBody DepositRequestDTO request){
        ApiResponse<DepositResponseDTO> savedAccount = accountService.depositCash(request.getAccountNumber(),request.getAmount());
        return new ResponseEntity<>(savedAccount,HttpStatus.OK);
    }

    @PostMapping("/accounts/withdraw")
    public ResponseEntity<ApiResponse<WithdrawResponseDTO>> depositsCash(@RequestBody WithdrawRequestDTO request){
        ApiResponse<WithdrawResponseDTO> savedAccount = accountService.withdrawCash(request.getAccountNumber(),request.getAmount());
        return new ResponseEntity<>(savedAccount,HttpStatus.OK);
    }

    @PostMapping("/accounts/transfer")
    public ResponseEntity<ApiResponse<com.bank.DigitalBank.DTO.TransferResponse>> transfer(@RequestBody TransferRequest request) {
        ApiResponse<com.bank.DigitalBank.DTO.TransferResponse> transferResponse = accountService.transferAmount(
                request.getFromAccount(),
                request.getToAccount(),
                request.getAmount()
        );



        return new ResponseEntity<>(transferResponse, HttpStatus.OK);
    }

    @GetMapping("/accounts/balance/{accountNumber}")
    public ResponseEntity<ApiResponse<BalanceDTO>> getBalance(@PathVariable String accountNumber){

        ApiResponse<BalanceDTO> account = accountService.getBalance(accountNumber);

        return new ResponseEntity<>(account,HttpStatus.OK);
    }

}
