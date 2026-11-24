package com.crio.rentvideo.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "videos")
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Director is required")
    @Column(nullable = false)
    private String director;

    @NotBlank(message = "Genre is required")
    @Column(nullable = false)
    private String genre;

    @Column(name = "availability_status", nullable = false)
    private Boolean availabilityStatus = true;

    public Video() {
    }

    public Video(String title, String director, String genre, Boolean availabilityStatus) {
        this.title = title;
        this.director = director;
        this.genre = genre;
        this.availabilityStatus = (availabilityStatus != null) ? availabilityStatus : true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Boolean getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(Boolean availabilityStatus) {
        this.availabilityStatus = (availabilityStatus != null) ? availabilityStatus : true;
    }
}
