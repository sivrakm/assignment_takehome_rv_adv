package com.crio.rentvideo.service;

import com.crio.rentvideo.dto.VideoRequest;
import com.crio.rentvideo.entity.Video;
import com.crio.rentvideo.exception.ResourceNotFoundException;
import com.crio.rentvideo.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VideoService {

    private final VideoRepository videoRepository;

    @Autowired
    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @Transactional
    public Video createVideo(VideoRequest request) {
        Boolean availability = (request.getAvailabilityStatus() != null) ? request.getAvailabilityStatus() : true;
        Video video = new Video(
                request.getTitle(),
                request.getDirector(),
                request.getGenre(),
                availability
        );
        return videoRepository.save(video);
    }

    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }

    public List<Video> getAvailableVideos() {
        return videoRepository.findByAvailabilityStatus(true);
    }

    public Video getVideoById(Long id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found with id: " + id));
    }

    @Transactional
    public Video updateVideo(Long id, VideoRequest request) {
        Video existingVideo = getVideoById(id);

        existingVideo.setTitle(request.getTitle());
        existingVideo.setDirector(request.getDirector());
        existingVideo.setGenre(request.getGenre());
        if (request.getAvailabilityStatus() != null) {
            existingVideo.setAvailabilityStatus(request.getAvailabilityStatus());
        }

        return videoRepository.save(existingVideo);
    }

    @Transactional
    public void deleteVideo(Long id) {
        Video existingVideo = getVideoById(id);
        videoRepository.delete(existingVideo);
    }
}
