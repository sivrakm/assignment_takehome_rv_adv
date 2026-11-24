package com.crio.rentvideo.service;

import com.crio.rentvideo.dto.RentalResponse;
import com.crio.rentvideo.entity.Rental;
import com.crio.rentvideo.entity.RentalStatus;
import com.crio.rentvideo.entity.Role;
import com.crio.rentvideo.entity.User;
import com.crio.rentvideo.entity.Video;
import com.crio.rentvideo.exception.BadRequestException;
import com.crio.rentvideo.repository.RentalRepository;
import com.crio.rentvideo.repository.UserRepository;
import com.crio.rentvideo.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RentalService rentalService;

    private User sampleUser;
    private Video sampleVideo;

    @BeforeEach
    void setUp() {
        sampleUser = new User("user@example.com", "pass123", "John", "Doe", Role.CUSTOMER);
        sampleUser.setId(1L);

        sampleVideo = new Video("Inception", "Christopher Nolan", "Sci-Fi", true);
        sampleVideo.setId(10L);
    }

    @Test
    void testRentVideo_Success() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(sampleUser));
        when(videoRepository.findById(10L)).thenReturn(Optional.of(sampleVideo));
        when(rentalRepository.countByUserAndStatus(sampleUser, RentalStatus.ACTIVE)).thenReturn(0L);

        Rental savedRental = new Rental(sampleUser, sampleVideo);
        savedRental.setId(100L);
        when(rentalRepository.save(any(Rental.class))).thenReturn(savedRental);

        RentalResponse response = rentalService.rentVideo(10L, "user@example.com");

        assertNotNull(response);
        assertEquals(10L, response.getVideoId());
        assertEquals("Inception", response.getVideoTitle());
        assertFalse(sampleVideo.getAvailabilityStatus());
        verify(videoRepository, times(1)).save(sampleVideo);
        verify(rentalRepository, times(1)).save(any(Rental.class));
    }

    @Test
    void testRentVideo_MaxActiveRentalsExceeded_ThrowsBadRequest() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(sampleUser));
        when(videoRepository.findById(10L)).thenReturn(Optional.of(sampleVideo));
        when(rentalRepository.countByUserAndStatus(sampleUser, RentalStatus.ACTIVE)).thenReturn(2L);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> rentalService.rentVideo(10L, "user@example.com"));

        assertTrue(ex.getMessage().contains("more than 2 active rentals"));
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    void testRentVideo_VideoNotAvailable_ThrowsBadRequest() {
        sampleVideo.setAvailabilityStatus(false);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(sampleUser));
        when(videoRepository.findById(10L)).thenReturn(Optional.of(sampleVideo));
        when(rentalRepository.countByUserAndStatus(sampleUser, RentalStatus.ACTIVE)).thenReturn(0L);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> rentalService.rentVideo(10L, "user@example.com"));

        assertTrue(ex.getMessage().contains("not available for rent"));
        verify(rentalRepository, never()).save(any(Rental.class));
    }

    @Test
    void testReturnVideo_Success() {
        sampleVideo.setAvailabilityStatus(false);
        Rental activeRental = new Rental(sampleUser, sampleVideo);
        activeRental.setId(100L);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(sampleUser));
        when(videoRepository.findById(10L)).thenReturn(Optional.of(sampleVideo));
        when(rentalRepository.findFirstByUserAndVideoAndStatus(sampleUser, sampleVideo, RentalStatus.ACTIVE))
                .thenReturn(Optional.of(activeRental));
        when(rentalRepository.save(any(Rental.class))).thenReturn(activeRental);

        RentalResponse response = rentalService.returnVideo(10L, "user@example.com");

        assertNotNull(response);
        assertEquals(RentalStatus.RETURNED, activeRental.getStatus());
        assertNotNull(activeRental.getReturnDate());
        assertTrue(sampleVideo.getAvailabilityStatus());
        verify(videoRepository, times(1)).save(sampleVideo);
        verify(rentalRepository, times(1)).save(activeRental);
    }

    @Test
    void testReturnVideo_NoActiveRental_ThrowsBadRequest() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(sampleUser));
        when(videoRepository.findById(10L)).thenReturn(Optional.of(sampleVideo));
        when(rentalRepository.findFirstByUserAndVideoAndStatus(sampleUser, sampleVideo, RentalStatus.ACTIVE))
                .thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> rentalService.returnVideo(10L, "user@example.com"));

        assertTrue(ex.getMessage().contains("No active rental found"));
    }
}
