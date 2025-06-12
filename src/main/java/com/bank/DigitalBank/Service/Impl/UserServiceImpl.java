package com.bank.DigitalBank.Service.Impl;

import com.bank.DigitalBank.Controller.BankController;
import com.bank.DigitalBank.Entity.User;
import com.bank.DigitalBank.Repository.UserRepo;
import com.bank.DigitalBank.Service.UserService;
import com.bank.DigitalBank.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private UserRepo userRepo;

    public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepo userRepo) {
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
    }

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public ApiResponse<User> register(User user) {


        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles("USER");
        }

        logger.info("Encoding Password");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userRepo.save(user);
        ApiResponse<User> savedUser = new ApiResponse(true,"User registered successfully",saved);

        logger.info("User Registered: "+ user.getName());
        return savedUser;
    }
}
