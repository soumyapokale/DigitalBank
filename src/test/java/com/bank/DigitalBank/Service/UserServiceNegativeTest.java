package com.bank.DigitalBank.Service;

import com.bank.DigitalBank.Entity.User;
import com.bank.DigitalBank.Repository.UserRepo;
import com.bank.DigitalBank.Service.CustomUserDetailsService;
import com.bank.DigitalBank.Service.Impl.UserServiceImpl;
import com.bank.DigitalBank.Utils.JwtUtil;
import com.bank.DigitalBank.config.ModelMapperConfig;
import com.bank.DigitalBank.dto.LoginRequest;
import com.bank.DigitalBank.dto.UserDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.modelmapper.ModelMapper;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceNegativeTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserRepo userRepo;
    //@Mock private ModelMapperConfig modelMapperConfig;
    @Mock private ModelMapper modelMapper;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtUtil jwtUtil;
    @Mock private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setup() throws Exception {
        // Setup ModelMapper
        //when(modelMapperConfig.modelMapper()).thenReturn(modelMapper);

        // Create instance using constructor


        // Inject autowired fields manually via reflection
        injectField(userService, "authenticationManager", authenticationManager);
        injectField(userService, "jwtUtil", jwtUtil);
        injectField(userService, "userDetailsService", userDetailsService);
    }

    // 🔧 Utility method for reflection-based field injection
    private void injectField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void login_shouldFailWhenEmailIsNull() throws Exception {
        LoginRequest request = new LoginRequest(null, "password");
        Exception ex = assertThrows(Exception.class, () -> userService.login(request));
        assertEquals("Please enter a valid email and password", ex.getMessage());
    }

    @Test
    void login_shouldFailWhenPasswordIsEmpty() throws Exception {
        LoginRequest request = new LoginRequest("user@example.com", "");
        Exception ex = assertThrows(Exception.class, () -> userService.login(request));
        assertEquals("Please enter a valid email and password", ex.getMessage());
    }

    @Test
    void login_shouldFailWhenAuthenticationFails() throws Exception {
        LoginRequest request = new LoginRequest("user@example.com", "wrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        Exception ex = assertThrows(Exception.class, () -> userService.login(request));
        assertEquals("Invalid email or password", ex.getMessage());
    }

    @Test
    void login_shouldFailWhenRequestIsNull() {
        Exception ex = assertThrows(Exception.class, () -> {
            userService.login(null);
        });
        assertEquals("Please enter a valid email and password", ex.getMessage());
    }

    @Test
    void register_shouldFailWhenUserDtoIsNull() {
        assertThrows(NullPointerException.class, () -> userService.register(null));
    }
}
