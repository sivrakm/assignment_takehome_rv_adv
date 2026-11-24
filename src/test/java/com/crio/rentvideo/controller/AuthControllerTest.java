package com.crio.rentvideo.controller;

import com.crio.rentvideo.config.SecurityConfig;
import com.crio.rentvideo.dto.RegisterRequest;
import com.crio.rentvideo.dto.UserResponse;
import com.crio.rentvideo.entity.Role;
import com.crio.rentvideo.security.CustomAccessDeniedHandler;
import com.crio.rentvideo.security.CustomAuthenticationEntryPoint;
import com.crio.rentvideo.security.CustomUserDetailsService;
import com.crio.rentvideo.security.JwtAuthenticationFilter;
import com.crio.rentvideo.security.JwtUtils;
import com.crio.rentvideo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, CustomAuthenticationEntryPoint.class, CustomAccessDeniedHandler.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegisterUser_PublicEndpoint() throws Exception {
        RegisterRequest request = new RegisterRequest("user@example.com", "password123", "John", "Doe", Role.CUSTOMER);
        UserResponse response = new UserResponse(1L, "user@example.com", "John", "Doe", Role.CUSTOMER);

        when(userService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }
}
