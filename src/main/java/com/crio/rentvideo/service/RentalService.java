package com.crio.rentvideo.service;

import com.crio.rentvideo.dto.RentalResponse;
import com.crio.rentvideo.entity.Rental;
import com.crio.rentvideo.entity.RentalStatus;
import com.crio.rentvideo.entity.User;
import com.crio.rentvideo.entity.Video;
import com.crio.rentvideo.exception.BadRequestException;
import com.crio.rentvideo.exception.ResourceNotFoundException;
import com.crio.rentvideo.repository.RentalRepository;
import com.crio.rentvideo.repository.UserRepository;
import com.crio.rentvideo.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RentalService {

    private final RentalRepository rentalRepository;
    private final VideoRepository videoRepository;
    private final UserRepository userRepository;

    @Autowired
    public RentalService(RentalRepository rentalRepository,
                         VideoRepository videoRepository,
                         UserRepository userRepository) {
        this.rentalRepository = rentalRepository;
        this.videoRepository = videoRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public RentalResponse rentVideo(Long videoId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found with id: " + videoId));

        // Check if user already has 2 active rentals
        long activeCount = rentalRepository.countByUserAndStatus(user, RentalStatus.ACTIVE);
        if (activeCount >= 2) {
            throw new BadRequestException("User cannot have more than 2 active rentals");
        }

        // Check if video is available
        if (Boolean.FALSE.equals(video.getAvailabilityStatus())) {
            throw new BadRequestException("Video is not available for rent");
        }

        // Mark video as unavailable
        video.setAvailabilityStatus(false);
        videoRepository.save(video);

        // Create rental
        Rental rental = new Rental(user, video);
        Rental savedRental = rentalRepository.save(rental);

        return RentalResponse.fromRental(savedRental, "Video rented successfully");
    }

    @Transactional
    public RentalResponse returnVideo(Long videoId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found with id: " + videoId));

        Rental rental = rentalRepository.findFirstByUserAndVideoAndStatus(user, video, RentalStatus.ACTIVE)
                .orElseThrow(() -> new BadRequestException("No active rental found for video id: " + videoId + " for this user"));

        rental.setStatus(RentalStatus.RETURNED);
        rental.setReturnDate(LocalDateTime.now());
        Rental updatedRental = rentalRepository.save(rental);

        video.setAvailabilityStatus(true);
        videoRepository.save(video);

        return RentalResponse.fromRental(updatedRental, "Video returned successfully");
    }

    public List<RentalResponse> getUserRentals(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        return rentalRepository.findByUser(user).stream()
                .map(r -> RentalResponse.fromRental(r, null))
                .collect(Collectors.toList());
    }

    public List<RentalResponse> getAllRentals() {
        return rentalRepository.findAll().stream()
                .map(r -> RentalResponse.fromRental(r, null))
                .collect(Collectors.toList());
    }
}
