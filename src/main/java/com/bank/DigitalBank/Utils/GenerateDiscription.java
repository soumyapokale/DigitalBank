package com.bank.DigitalBank.Utils;

import com.bank.DigitalBank.Entity.Transaction;
import com.bank.DigitalBank.Entity.enums.TransactionType;

public class GenerateDiscription {

    public static String generateDescription(Transaction txn, String accountNumber) {
        if (txn.getTransactionType() == TransactionType.DEBIT) {
            return "Transfer to " + txn.getToAccount();
        } else if (txn.getTransactionType() == TransactionType.CREDIT) {
            return "Transfer from " + txn.getFromAccount();
        } else if (txn.getTransactionType() == TransactionType.DEPOSIT) {
            return "Cash Deposit";
        } else if (txn.getTransactionType() == TransactionType.WITHDRAWAL) {
            return "Cash Withdrawal";
        }
        return "Transaction";
    }

}
