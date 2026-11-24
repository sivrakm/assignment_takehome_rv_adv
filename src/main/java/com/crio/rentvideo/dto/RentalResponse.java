package com.crio.rentvideo.dto;

import com.crio.rentvideo.entity.Rental;
import com.crio.rentvideo.entity.RentalStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RentalResponse {

    private Long id;
    private Long videoId;
    private String videoTitle;
    private Long userId;
    private String userEmail;
    private LocalDateTime rentalDate;
    private LocalDateTime returnDate;
    private RentalStatus status;
    private String message;

    public RentalResponse() {
    }

    public RentalResponse(Rental rental, String message) {
        if (rental != null) {
            this.id = rental.getId();
            if (rental.getVideo() != null) {
                this.videoId = rental.getVideo().getId();
                this.videoTitle = rental.getVideo().getTitle();
            }
            if (rental.getUser() != null) {
                this.userId = rental.getUser().getId();
                this.userEmail = rental.getUser().getEmail();
            }
            this.rentalDate = rental.getRentalDate();
            this.returnDate = rental.getReturnDate();
            this.status = rental.getStatus();
        }
        this.message = message;
    }

    public static RentalResponse fromRental(Rental rental, String message) {
        return new RentalResponse(rental, message);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public LocalDateTime getRentalDate() {
        return rentalDate;
    }

    public void setRentalDate(LocalDateTime rentalDate) {
        this.rentalDate = rentalDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public RentalStatus getStatus() {
        return status;
    }

    public void setStatus(RentalStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
