package com.bank.DigitalBank.Service.Impl;


import com.bank.DigitalBank.Entity.User;
import com.bank.DigitalBank.Repository.UserRepo;
import com.bank.DigitalBank.Service.UserService;
import com.bank.DigitalBank.config.ModelMapperConfig;
import com.bank.DigitalBank.dto.ApiResponse;
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
}
