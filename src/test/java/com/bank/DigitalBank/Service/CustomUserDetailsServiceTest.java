package com.bank.DigitalBank.Service;

import com.bank.DigitalBank.Entity.User;
import com.bank.DigitalBank.Repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CustomUserDetailsServiceTest {

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private UserRepo userRepo;

    @Test
    public void loadUserByUsernameTest(){

        User mappedUser = new User();
        mappedUser.setId(1L);
        mappedUser.setName("Soumya");
        mappedUser.setEmail("soumyapokale@gmail.com");
        mappedUser.setPassword("encodedPassword"); // simulate encoded password
        mappedUser.setRoles("ADMIN");

        when(userRepo.findByEmail(mappedUser.getEmail())).thenReturn(mappedUser);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(mappedUser.getEmail());


        assertEquals(userDetails.getUsername(),"soumyapokale@gmail.com");
        assertEquals(userDetails.getPassword(),"encodedPassword");
        assertTrue(userDetails.getAuthorities().stream().anyMatch(auth->auth.getAuthority().equals("ROLE_ADMIN")));



    }


}