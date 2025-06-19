package com.bank.DigitalBank.Repository;

import com.bank.DigitalBank.Entity.Transaction;
import com.bank.DigitalBank.Entity.enums.TransactionType;
import com.bank.DigitalBank.dto.TransactionDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction,Long> {
    

    List<Transaction> findByToAccount(String accountNumber);

    List<Transaction> findByFromAccount(String accountNumber);



    @Query("SELECT t FROM Transaction t WHERE t.toAccount = :accountNumber AND t.transactionType = :type")
    List<Transaction> findByToAccountAndType(@Param("accountNumber") String accountNumber,
                                             @Param("type") TransactionType type);

    List<Transaction> findTop5ByFromAccountOrToAccountOrderByTransactionDateDesc(String from, String to);


}
