package com.crio.rentvideo.controller;

import com.crio.rentvideo.config.SecurityConfig;
import com.crio.rentvideo.dto.VideoRequest;
import com.crio.rentvideo.entity.Video;
import com.crio.rentvideo.security.CustomAccessDeniedHandler;
import com.crio.rentvideo.security.CustomAuthenticationEntryPoint;
import com.crio.rentvideo.security.CustomUserDetailsService;
import com.crio.rentvideo.security.JwtAuthenticationFilter;
import com.crio.rentvideo.security.JwtUtils;
import com.crio.rentvideo.service.VideoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VideoController.class)
@Import({SecurityConfig.class, CustomAuthenticationEntryPoint.class, CustomAccessDeniedHandler.class})
class VideoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VideoService videoService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetVideos_Unauthenticated_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/videos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = {"CUSTOMER"})
    void testGetVideos_CustomerRole_Success() throws Exception {
        Video video = new Video("Video One", "Director One", "Genre One", true);
        video.setId(1L);

        when(videoService.getAllVideos()).thenReturn(Collections.singletonList(video));

        mockMvc.perform(get("/api/videos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Video One"));
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = {"CUSTOMER"})
    void testCreateVideo_CustomerRole_ReturnsForbidden() throws Exception {
        VideoRequest request = new VideoRequest("Video New", "Director New", "Genre New", true);

        mockMvc.perform(post("/api/videos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void testCreateVideo_AdminRole_ReturnsCreated() throws Exception {
        VideoRequest request = new VideoRequest("Video New", "Director New", "Genre New", true);
        Video created = new Video("Video New", "Director New", "Genre New", true);
        created.setId(2L);

        when(videoService.createVideo(any(VideoRequest.class))).thenReturn(created);

        mockMvc.perform(post("/api/videos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.title").value("Video New"));
    }
}
