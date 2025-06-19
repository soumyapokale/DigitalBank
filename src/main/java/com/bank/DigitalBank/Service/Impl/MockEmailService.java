package com.bank.DigitalBank.Service.Impl;

import com.bank.DigitalBank.Service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MockEmailService implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(MockEmailService.class);

    @Override
    public void sendEmail(String to, String subject, String body) {
        logger.info("Simulated Email Sent:");
        logger.info("To      : {}", to);
        logger.info("Subject : {}", subject);
        logger.info("Body    : {}", body);
    }
}
