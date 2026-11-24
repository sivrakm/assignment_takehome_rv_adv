package com.crio.rentvideo.service;

import com.crio.rentvideo.dto.VideoRequest;
import com.crio.rentvideo.entity.Video;
import com.crio.rentvideo.exception.ResourceNotFoundException;
import com.crio.rentvideo.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoServiceTest {

    @Mock
    private VideoRepository videoRepository;

    @InjectMocks
    private VideoService videoService;

    private Video sampleVideo;

    @BeforeEach
    void setUp() {
        sampleVideo = new Video("Inception", "Christopher Nolan", "Sci-Fi", true);
        sampleVideo.setId(10L);
    }

    @Test
    void testCreateVideo_DefaultsToAvailable() {
        VideoRequest request = new VideoRequest("Interstellar", "Christopher Nolan", "Sci-Fi", null);

        Video created = new Video("Interstellar", "Christopher Nolan", "Sci-Fi", true);
        created.setId(11L);

        when(videoRepository.save(any(Video.class))).thenReturn(created);

        Video result = videoService.createVideo(request);

        assertNotNull(result);
        assertEquals("Interstellar", result.getTitle());
        assertTrue(result.getAvailabilityStatus());
    }

    @Test
    void testGetAllVideos() {
        when(videoRepository.findAll()).thenReturn(Arrays.asList(sampleVideo));

        List<Video> list = videoService.getAllVideos();
        assertEquals(1, list.size());
        assertEquals("Inception", list.get(0).getTitle());
    }

    @Test
    void testGetAvailableVideos() {
        when(videoRepository.findByAvailabilityStatus(true)).thenReturn(Arrays.asList(sampleVideo));

        List<Video> list = videoService.getAvailableVideos();
        assertEquals(1, list.size());
        assertTrue(list.get(0).getAvailabilityStatus());
    }

    @Test
    void testGetVideoById_Found() {
        when(videoRepository.findById(10L)).thenReturn(Optional.of(sampleVideo));

        Video video = videoService.getVideoById(10L);
        assertNotNull(video);
        assertEquals("Inception", video.getTitle());
    }

    @Test
    void testGetVideoById_NotFoundThrowsException() {
        when(videoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> videoService.getVideoById(99L));
    }

    @Test
    void testUpdateVideo_Success() {
        VideoRequest updateReq = new VideoRequest("Inception 2", "Christopher Nolan", "Sci-Fi", false);
        when(videoRepository.findById(10L)).thenReturn(Optional.of(sampleVideo));
        when(videoRepository.save(any(Video.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Video updated = videoService.updateVideo(10L, updateReq);

        assertEquals("Inception 2", updated.getTitle());
        assertFalse(updated.getAvailabilityStatus());
    }

    @Test
    void testDeleteVideo_Success() {
        when(videoRepository.findById(10L)).thenReturn(Optional.of(sampleVideo));
        doNothing().when(videoRepository).delete(sampleVideo);

        assertDoesNotThrow(() -> videoService.deleteVideo(10L));
        verify(videoRepository, times(1)).delete(sampleVideo);
    }
}
