package com.bank.DigitalBank.Service;


import com.bank.DigitalBank.dto.ApiResponse;
import com.bank.DigitalBank.dto.LoginRequest;
import com.bank.DigitalBank.dto.UserDto;
import org.springframework.stereotype.Service;

@Service

public interface UserService {
    ApiResponse<UserDto> register(UserDto user);

    ApiResponse<String> login(LoginRequest request) throws Exception;
}
