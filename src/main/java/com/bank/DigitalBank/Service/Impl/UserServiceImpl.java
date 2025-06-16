package com.bank.DigitalBank.Service.Impl;


import com.bank.DigitalBank.Entity.User;
import com.bank.DigitalBank.Repository.UserRepo;
import com.bank.DigitalBank.Service.UserService;
import com.bank.DigitalBank.config.ModelMapperConfig;
import com.bank.DigitalBank.dto.ApiResponse;
import com.bank.DigitalBank.dto.LoginRequest;
import com.bank.DigitalBank.dto.LoginResponse;
import com.bank.DigitalBank.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;

    private final ModelMapperConfig modelMapperConfig;
    public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepo userRepo, ModelMapperConfig modelMapperConfig) {
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
        this.modelMapperConfig = modelMapperConfig;
    }

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public ApiResponse<UserDto> register(UserDto user) {


        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles("USER");
        }

        logger.info("Encoding Password");
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User registerUser = modelMapperConfig.modelMapper().map(user,User.class);
        User saved = userRepo.save(registerUser);

        ApiResponse<UserDto> savedUser = new ApiResponse(true,"User registered successfully",modelMapperConfig.modelMapper().map(saved,UserDto.class));

        logger.info("User Registered: "+ user.getName());
        return savedUser;
    }

    @Override
    public ApiResponse<LoginResponse> login(LoginRequest request) throws Exception {
        if (request.getEmail() == null || request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new Exception("Please enter a valid email and password");
        }

        User user = userRepo.findByEmail(request.getEmail());
        if (user == null) {
            throw new IllegalArgumentException("User not found with email: " + request.getEmail());
        }

        // Compare encoded password with raw password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        // Create response DTO
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setId(user.getId());
        loginResponse.setName(user.getName());
        loginResponse.setEmail(user.getEmail());

        return new ApiResponse<>(true, "Login successful", loginResponse);
    }
}
