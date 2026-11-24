package com.crio.rentvideo.controller;

import com.crio.rentvideo.dto.VideoRequest;
import com.crio.rentvideo.entity.Video;
import com.crio.rentvideo.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping({"/api/videos", "/videos"})
public class VideoController {

    private final VideoService videoService;

    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping
    public ResponseEntity<List<Video>> getVideos(@RequestParam(required = false) Boolean available) {
        if (Boolean.TRUE.equals(available)) {
            return ResponseEntity.ok(videoService.getAvailableVideos());
        }
        return ResponseEntity.ok(videoService.getAllVideos());
    }

    @GetMapping("/available")
    public ResponseEntity<List<Video>> getAvailableVideos() {
        return ResponseEntity.ok(videoService.getAvailableVideos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Video> getVideoById(@PathVariable Long id) {
        Video video = videoService.getVideoById(id);
        return ResponseEntity.ok(video);
    }

    @PostMapping
    public ResponseEntity<Video> createVideo(@Valid @RequestBody VideoRequest request) {
        Video created = videoService.createVideo(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Video> updateVideo(@PathVariable Long id, @Valid @RequestBody VideoRequest request) {
        Video updated = videoService.updateVideo(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id) {
        videoService.deleteVideo(id);
        return ResponseEntity.noContent().build();
    }
}
