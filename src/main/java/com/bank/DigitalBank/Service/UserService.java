package com.bank.DigitalBank.Service;


import com.bank.DigitalBank.Entity.User;
import com.bank.DigitalBank.dto.ApiResponse;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    ApiResponse<User> register(User user);
}
