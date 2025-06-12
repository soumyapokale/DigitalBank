package com.bank.DigitalBank.Mapper;


import com.bank.DigitalBank.Entity.Account;
import com.bank.DigitalBank.Entity.User;
import com.bank.DigitalBank.dto.AccountDto;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


@Component
public class AccountMapper {

    public AccountDto toAccountDto(Account account){

        AccountDto accountDto = new AccountDto(account.getId(), account.getAccountNumber(), account.getBalance(),account.getCreatedAt(),account.getUser().getId());


        return accountDto;
    }

    public Account toAccount(AccountDto accountDto){

        User user = new User();
        user.setId(accountDto.getUserId());
        Account account = new Account(accountDto.getId(), accountDto.getAccountNumber(), accountDto.getBalance(),accountDto.getCreatedAt(),user);

        return account;
    }
}
