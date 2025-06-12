package com.bank.DigitalBank.Utils;

import java.util.Random;

public class AccountNumGenerator {

    public static String generateAccountNumberWithPrefix() {
        String prefix = "BOK"; // 3 characters
        int digitsNeeded = 12 - prefix.length(); // 9 digits
        Random random = new Random();
        StringBuilder sb = new StringBuilder(prefix);

        for (int i = 0; i < digitsNeeded; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString(); // e.g., "BOK928374652"
    }
}
