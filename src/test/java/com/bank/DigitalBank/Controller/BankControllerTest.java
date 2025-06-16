package com.bank.DigitalBank.Controller;

import com.bank.DigitalBank.Entity.enums.TransactionType;
import com.bank.DigitalBank.Service.AccountService;
import com.bank.DigitalBank.Service.UserService;
import com.bank.DigitalBank.Utils.JsonUtil;
import com.bank.DigitalBank.dto.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BankController.class)
class BankControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AccountService accountService; // ✅ Required for BankController

private LoginRequest loginRequest;

    private AccountDto validAccount;
    private UserDto validUser;

    @BeforeEach
    void setup() throws IOException {

        validUser = JsonUtil.readJsonFileAsObject("ValidUser.json", UserDto.class);
        validAccount = JsonUtil.readJsonFileAsObject("ValidAccount.json", AccountDto.class);



    }

    @Test
    public void testRegisterUser() throws Exception {
        ApiResponse<UserDto> apiResponse = new ApiResponse<>(true, "User registered successfully", validUser);
        when(userService.register(any(UserDto.class))).thenReturn(apiResponse);

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.readJsonFileAsString("/ValidUser.json")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.data.name").value(validUser.getName()));
    }


    @Test
    public void testRegisterAccount() throws Exception{
        ApiResponse<AccountDto> apiResponse = new ApiResponse<>(true,"success",validAccount);
        when(accountService.register(any(AccountDto.class))).thenReturn(apiResponse);
        mockMvc.perform(post("/api/accounts/create").contentType(MediaType.APPLICATION_JSON).content(JsonUtil.readJsonFileAsString("/ValidAccount.json"))).andExpect(jsonPath("$.success").value(true)).andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.userId").value(validAccount.getUserId()));
    }

    @Test
    public void testDepositAccount() throws Exception {
        // Create a deposit request DTO
        DepositRequestDTO requestDTO = new DepositRequestDTO();
        requestDTO.setAccountNumber("SBIN00112233");
        requestDTO.setAmount(new BigDecimal("1000.00"));

        // Create a simulated response
        DepositResponseDTO responseDTO = new DepositResponseDTO();
        responseDTO.setAccountNumber("SBIN00112233");
        responseDTO.setNewBalance(new BigDecimal("6000.00")); // simulated new balance

        ApiResponse<DepositResponseDTO> apiResponse =
                new ApiResponse<>(true, "Deposit successful", responseDTO);

        // Mock the service call
        when(accountService.depositCash(
                        requestDTO.getAccountNumber(), requestDTO.getAmount()))
                .thenReturn(apiResponse);

        // Perform POST request
        mockMvc.perform(post("/api/accounts/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Deposit successful"))
                .andExpect(jsonPath("$.data.accountNumber").value("SBIN00112233"))
                .andExpect(jsonPath("$.data.newBalance").value(6000.00)); // ✅ correct

    }
    @Test
    public void testWithdrawMoney() throws Exception{
        WithdrawRequestDTO requestDTO = new WithdrawRequestDTO();
        requestDTO.setAccountNumber("SBIN00112233");
        requestDTO.setAmount(new BigDecimal("1000.00"));

        WithdrawResponseDTO responseDTO = new WithdrawResponseDTO();
        responseDTO.setAccountNumber("SBIN00112233");
        responseDTO.setNewBalance(new BigDecimal("2000.00")); // simulated new balance

        ApiResponse<WithdrawResponseDTO> response = new ApiResponse<>(true,"success",responseDTO);

        when(accountService.withdrawCash(anyString(),any(BigDecimal.class))).thenReturn(response);

        mockMvc.perform(post("/api/accounts/withdraw").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(requestDTO))).
                andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.accountNumber").value("SBIN00112233"))
                .andExpect(jsonPath("$.data.newBalance").value(2000.00));

    }

    @Test
    public void testTransfer() throws Exception{
        TransferRequest request = new TransferRequest();

        request.setAmount(new BigDecimal(1000));
        request.setFromAccount("SBIN00112233");
        request.setToAccount("SBIN00112234");

        TransferResponse transferResponse = new TransferResponse();
        transferResponse.setTransferredAmount(new BigDecimal(1000));
        transferResponse.setFromAccount("SBIN00112233");
        transferResponse.setToAccount("SBIN00112234");

        ApiResponse<TransferResponse> apiResponse = new ApiResponse<>(true,"success",transferResponse);

        when(accountService.transferAmount(request.getFromAccount(),request.getToAccount(),request.getAmount())).thenReturn(apiResponse);

        mockMvc.perform(post("/api/accounts/transfer").contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request))).
                andExpect(jsonPath("$.success").value(true)).
                andExpect(jsonPath("$.message").value("success")).
                andExpect(jsonPath("$.data.fromAccount").value("SBIN00112233")).
                andExpect(jsonPath("$.data.toAccount").value("SBIN00112234")).
                andExpect(status().isOk());
    }

    @Test
    public void testBalance() throws Exception{

        BalanceDTO balanceDTO = new BalanceDTO();
        balanceDTO.setBalance(new BigDecimal(10000));
        balanceDTO.setAccountNumber("SBIN00112234");

        ApiResponse<BalanceDTO> apiResponse = new ApiResponse<>(true,"success",balanceDTO);
        when(accountService.getBalance(anyString())).thenReturn(apiResponse);

        mockMvc.perform(get("/api/accounts/balance/{accountNumber}", balanceDTO.getAccountNumber())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accountNumber").value(balanceDTO.getAccountNumber()))
                .andExpect(jsonPath("$.data.balance").value(balanceDTO.getBalance()));

    }

    @Test
    public  void login() throws Exception {
        loginRequest = new LoginRequest("soumyapokale@gmail.com","statebank");

        LoginResponse loginResponse = new LoginResponse(1L,"soumyapokale","soumyapokale@gmail.com");

        ApiResponse<LoginResponse> loginresponse= new ApiResponse<>(true,"success",loginResponse);

        when(userService.login(any(LoginRequest.class))).thenReturn(loginresponse);
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("soumyapokale@gmail.com"));

}

    @Test
    public void transferHistory() throws Exception {

        String accountNumber = "SKI101010023";

        TrasactionResponse tx1 = new TrasactionResponse(
                1L,
                TransactionType.DEPOSIT,
                new BigDecimal("1000.00"),
                LocalDateTime.now().minusDays(1)
        );

        TrasactionResponse tx2 = new TrasactionResponse(
                1L,
                TransactionType.WITHDRAWAL,
                new BigDecimal("1000.00"),
                LocalDateTime.now().minusDays(1)
        );
        TrasactionResponse tx3 = new TrasactionResponse(
                3L,
                TransactionType.TRANSFER,
                new BigDecimal("750.00"),
                LocalDateTime.now()
        );

        List<TrasactionResponse> response = List.of(tx1,tx2,tx3);

        ApiResponse<List<TrasactionResponse>> apiResponse= new ApiResponse<>(true,"SUCCESS",response);

        when(accountService.getTransactionHistory(accountNumber)).thenReturn(apiResponse);

        mockMvc.perform(get("/api/users/transaction/{accountNumber}", accountNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("SUCCESS"))
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.data[0].transactionType").value("DEPOSIT"))
                .andExpect(jsonPath("$.data[1].transactionType").value("WITHDRAWAL"))
                .andExpect(jsonPath("$.data[2].transactionType").value("TRANSFER"));

    }

    @Test
    public void accountSummaryHistoryTest() throws Exception {

        AccountSummaryDTO accountSummaryDTO = new AccountSummaryDTO("SKI101010023","soumya",new BigDecimal(1000),new BigDecimal(1234),new BigDecimal(1567),new BigDecimal(2222));

        ApiResponse<AccountSummaryDTO> response= new ApiResponse<>(true,"SUCCESS",accountSummaryDTO);
       when(accountService.getAccountSummary(accountSummaryDTO.getAccountNumber())).thenReturn(response);
        mockMvc.perform(get("/api/users/account/{accountNumber}/summary",accountSummaryDTO.getAccountNumber()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("SUCCESS"))
                .andExpect(jsonPath("$.data.accountNumber").value("SKI101010023"))
                .andExpect(jsonPath("$.data.holderName").value("soumya"))
                .andExpect(jsonPath("$.data.balance").value(1000))
                .andExpect(jsonPath("$.data.totalDeposits").value(1234))
                .andExpect(jsonPath("$.data.totalWithdrawals").value(1567))
                .andExpect(jsonPath("$.data.totalTransfers").value(2222));



    }
}
