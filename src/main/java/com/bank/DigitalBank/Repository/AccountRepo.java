package com.bank.DigitalBank.Repository;

import com.bank.DigitalBank.Entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AccountRepo extends JpaRepository<Account,Long> {
    Account findByAccountNumber(String accountNumber);


}
