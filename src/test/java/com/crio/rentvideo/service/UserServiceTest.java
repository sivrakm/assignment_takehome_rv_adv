package com.crio.rentvideo.service;

import com.crio.rentvideo.dto.RegisterRequest;
import com.crio.rentvideo.dto.UserResponse;
import com.crio.rentvideo.entity.Role;
import com.crio.rentvideo.entity.User;
import com.crio.rentvideo.exception.UserAlreadyExistsException;
import com.crio.rentvideo.repository.UserRepository;
import com.crio.rentvideo.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserService userService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("alice@example.com");
        registerRequest.setPassword("securepass123");
        registerRequest.setFirstName("Alice");
        registerRequest.setLastName("Wonderland");
    }

    @Test
    void testRegisterUser_SuccessWithDefaultCustomerRole() {
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode("securepass123")).thenReturn("hashedPassword");

        User saved = new User("alice@example.com", "hashedPassword", "Alice", "Wonderland", Role.CUSTOMER);
        saved.setId(1L);
        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserResponse response = userService.register(registerRequest);

        assertNotNull(response);
        assertEquals("alice@example.com", response.getEmail());
        assertEquals(Role.CUSTOMER, response.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_WithExplicitAdminRole() {
        registerRequest.setRole(Role.ADMIN);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode("securepass123")).thenReturn("hashedPassword");

        User saved = new User("alice@example.com", "hashedPassword", "Alice", "Wonderland", Role.ADMIN);
        saved.setId(2L);
        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserResponse response = userService.register(registerRequest);

        assertNotNull(response);
        assertEquals(Role.ADMIN, response.getRole());
    }

    @Test
    void testRegisterUser_ThrowsExceptionWhenEmailExists() {
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }
}
