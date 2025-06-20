package com.bank.DigitalBank.Service;

import com.bank.DigitalBank.Entity.User;
import com.bank.DigitalBank.Repository.TransactionRepo;
import com.bank.DigitalBank.Repository.UserRepo;
import com.bank.DigitalBank.Service.Impl.UserServiceImpl;
import com.bank.DigitalBank.Utils.JsonUtil;
import com.bank.DigitalBank.config.ModelMapperConfig;
import com.bank.DigitalBank.dto.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)

class UserServiceNegativeTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepo userRepo;

    @Mock
    private TransactionRepo transactionRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapperConfig modelMapperConfig;

    @Mock
    private ModelMapper modelMapper;

    private UserDto validUser;

    @BeforeEach
    void setup() throws IOException {
        validUser = JsonUtil.readJsonFileAsObject("ValidUser.json", UserDto.class);
        when(modelMapperConfig.modelMapper()).thenReturn(modelMapper);
    }

    @Test
    void login_shouldFailWhenEmailIsNull() {
        LoginRequest request = new LoginRequest(null, "password");
        Exception ex = assertThrows(Exception.class, () -> userService.login(request));
        assertEquals("Please enter a valid email and password", ex.getMessage());
    }

    @Test
    void login_shouldFailWhenPasswordIsEmpty() {
        LoginRequest request = new LoginRequest("user@example.com", "");
        Exception ex = assertThrows(Exception.class, () -> userService.login(request));
        assertEquals("Please enter a valid email and password", ex.getMessage());
    }

    @Test
    void login_shouldFailWhenUserNotFound() {
        String email = "notfound@example.com";
        when(userRepo.findByEmail(email)).thenReturn(null);

        LoginRequest request = new LoginRequest(email, "password");
        Exception ex = assertThrows(IllegalArgumentException.class, () -> userService.login(request));
        assertEquals("User not found with email: " + email, ex.getMessage());
    }

    @Test
    void login_shouldFailWhenPasswordDoesNotMatch() {
        String email = "user@example.com";
        String rawPassword = "wrongPassword";

        User user = new User();
        user.setEmail(email);
        user.setPassword("encodedPassword");

        when(userRepo.findByEmail(email)).thenReturn(user);
        when(passwordEncoder.matches(rawPassword, user.getPassword())).thenReturn(false);

        LoginRequest request = new LoginRequest(email, rawPassword);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> userService.login(request));
        assertEquals("Invalid credentials", ex.getMessage());
    }

    @Test
    void register_shouldFailWhenUserDtoIsNull() {
        Exception ex = assertThrows(NullPointerException.class, () -> userService.register(null));
        assertTrue(ex.getMessage().contains("Cannot invoke")); // safer null check
    }

    @Test
    void login_shouldFailWhenRequestIsNull() {
        Exception ex = assertThrows(NullPointerException.class, () -> userService.login(null));
        assertTrue(ex.getMessage().contains("Cannot invoke")); // safer null check
    }
}
