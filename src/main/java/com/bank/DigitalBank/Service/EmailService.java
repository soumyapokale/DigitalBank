package com.bank.DigitalBank.Service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}
