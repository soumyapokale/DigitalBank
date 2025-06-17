package com.bank.DigitalBank.Service;

import com.bank.DigitalBank.Entity.User;
import com.bank.DigitalBank.Repository.TransactionRepo;
import com.bank.DigitalBank.Repository.UserRepo;
import com.bank.DigitalBank.Service.Impl.UserServiceImpl;
import com.bank.DigitalBank.Utils.JsonUtil;
import com.bank.DigitalBank.config.ModelMapperConfig;
import com.bank.DigitalBank.dto.ApiResponse;
import com.bank.DigitalBank.dto.LoginRequest;
import com.bank.DigitalBank.dto.LoginResponse;
import com.bank.DigitalBank.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)

class UserServiceTest {


    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepo userRepo;


    @Mock
    private TransactionRepo transactionRepo;
    @Mock
    private  PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapperConfig modelMapperConfig;
    @Mock
    private  ModelMapper modelMapper ;

    private UserDto validUser;

    private User mappedUser;

    @BeforeEach
    void setup() throws IOException {
        validUser = JsonUtil.readJsonFileAsObject("ValidUser.json", UserDto.class);
        mappedUser = new User();
        mappedUser.setId(1L);
        mappedUser.setName(validUser.getName());
        mappedUser.setEmail(validUser.getEmail());
        mappedUser.setPassword("encodedPassword"); // simulate encoded password
        mappedUser.setRoles(validUser.getRoles());
        when(modelMapperConfig.modelMapper()).thenReturn(modelMapper);
    }
    @Test
    public void RegisterUser(){
        when(passwordEncoder.encode(validUser.getPassword())).thenReturn("encodedPassword");

        // mock model mapper
        when(modelMapper.map(validUser, User.class)).thenReturn(mappedUser);
        when(userRepo.save(mappedUser)).thenReturn(mappedUser);
        when(modelMapper.map(mappedUser, UserDto.class)).thenReturn(validUser);

        // act
        ApiResponse<UserDto> response = userService.register(validUser);

        // assert
        assertTrue(response.isSuccess());
        assertEquals("User registered successfully", response.getMessage());
        assertEquals(validUser.getName(), response.getData().getName());
        assertEquals(validUser.getEmail(), response.getData().getEmail());
    }

    @Test
    public void LoginUser() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("soumyapokale@gmail.com", "encryptedpassword");
        User user = new User(1L, "soumyapokale", "soumyapokale@gmail.com", "encryptedpassword", "User");

        when(userRepo.findByEmail("soumyapokale@gmail.com")).thenReturn(user);
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);

        // Act
        ApiResponse<LoginResponse> response = userService.login(loginRequest);

        // Assert
        assertTrue(response.isSuccess());
        assertEquals("soumyapokale@gmail.com", response.getData().getEmail());
        assertEquals("soumyapokale", response.getData().getName());
        assertEquals("Login successful", response.getMessage());

        // Optional verifications
        verify(userRepo).findByEmail("soumyapokale@gmail.com");
        verify(passwordEncoder).matches("encryptedpassword", "encryptedpassword");
    }



}