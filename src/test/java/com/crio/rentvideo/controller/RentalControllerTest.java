package com.crio.rentvideo.controller;

import com.crio.rentvideo.config.SecurityConfig;
import com.crio.rentvideo.dto.RentalResponse;
import com.crio.rentvideo.entity.Rental;
import com.crio.rentvideo.entity.RentalStatus;
import com.crio.rentvideo.entity.Role;
import com.crio.rentvideo.entity.User;
import com.crio.rentvideo.entity.Video;
import com.crio.rentvideo.security.CustomAccessDeniedHandler;
import com.crio.rentvideo.security.CustomAuthenticationEntryPoint;
import com.crio.rentvideo.security.CustomUserDetailsService;
import com.crio.rentvideo.security.JwtAuthenticationFilter;
import com.crio.rentvideo.security.JwtUtils;
import com.crio.rentvideo.service.RentalService;
import com.crio.rentvideo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RentalController.class)
@Import({SecurityConfig.class, CustomAuthenticationEntryPoint.class, CustomAccessDeniedHandler.class})
class RentalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RentalService rentalService;

    @MockBean
    private UserService userService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtUtils jwtUtils;

    @Test
    void testRentVideo_Unauthenticated_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(post("/videos/1/rent"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = {"CUSTOMER"})
    void testRentVideo_Customer_Success() throws Exception {
        User user = new User("customer@example.com", "pass", "C", "User", Role.CUSTOMER);
        user.setId(1L);
        Video video = new Video("Inception", "Nolan", "Sci-Fi", true);
        video.setId(10L);

        Rental rental = new Rental(user, video);
        rental.setId(100L);
        RentalResponse response = RentalResponse.fromRental(rental, "Video rented successfully");

        when(rentalService.rentVideo(10L, "customer@example.com")).thenReturn(response);

        mockMvc.perform(post("/videos/10/rent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.videoTitle").value("Inception"))
                .andExpect(jsonPath("$.message").value("Video rented successfully"));
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = {"CUSTOMER"})
    void testReturnVideo_Customer_Success() throws Exception {
        User user = new User("customer@example.com", "pass", "C", "User", Role.CUSTOMER);
        user.setId(1L);
        Video video = new Video("Inception", "Nolan", "Sci-Fi", true);
        video.setId(10L);

        Rental rental = new Rental(user, video);
        rental.setId(100L);
        rental.setStatus(RentalStatus.RETURNED);
        RentalResponse response = RentalResponse.fromRental(rental, "Video returned successfully");

        // Testing the POST /books/{videoId}/return endpoint from requirements
        when(rentalService.returnVideo(10L, "customer@example.com")).thenReturn(response);

        mockMvc.perform(post("/books/10/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.status").value("RETURNED"))
                .andExpect(jsonPath("$.message").value("Video returned successfully"));
    }
}
