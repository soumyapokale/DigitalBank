package com.bank.DigitalBank.Service.Impl;


import com.bank.DigitalBank.Entity.User;
import com.bank.DigitalBank.Repository.UserRepo;
import com.bank.DigitalBank.Service.CustomUserDetailsService;
import com.bank.DigitalBank.Service.UserService;
import com.bank.DigitalBank.Utils.JwtUtil;
import com.bank.DigitalBank.config.ModelMapperConfig;
import com.bank.DigitalBank.dto.ApiResponse;
import com.bank.DigitalBank.dto.LoginRequest;
import com.bank.DigitalBank.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;


    private AuthenticationManager authenticationManager;


    private JwtUtil jwtUtil;



    private CustomUserDetailsService userDetailsService;



    private final ModelMapperConfig modelMapperConfig;
    public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepo userRepo, AuthenticationManager authenticationManager, JwtUtil jwtUtil, CustomUserDetailsService userDetailsService, ModelMapperConfig modelMapperConfig) {
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
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
    public ApiResponse<String> login(LoginRequest request) throws Exception {
        if (request == null || request.getEmail() == null || request.getPassword() == null || request.getEmail().isEmpty() || request.getPassword().isEmpty()) {
            throw new Exception("Please enter a valid email and password");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

            String token = jwtUtil.generateToken(
                    userDetails.getUsername(),
                    userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "")
            );

            return new ApiResponse<>(true, "Login Successful", token);

        } catch (BadCredentialsException e) {
            throw new Exception("Invalid email or password");
        }
    }

}
