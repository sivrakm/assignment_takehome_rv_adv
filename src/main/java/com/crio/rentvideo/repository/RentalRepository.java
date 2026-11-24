package com.crio.rentvideo.repository;

import com.crio.rentvideo.entity.Rental;
import com.crio.rentvideo.entity.RentalStatus;
import com.crio.rentvideo.entity.User;
import com.crio.rentvideo.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    List<Rental> findByUser(User user);

    List<Rental> findByUserAndStatus(User user, RentalStatus status);

    long countByUserAndStatus(User user, RentalStatus status);

    Optional<Rental> findFirstByUserAndVideoAndStatus(User user, Video video, RentalStatus status);

    Optional<Rental> findFirstByVideoAndStatus(Video video, RentalStatus status);

    List<Rental> findByUserId(Long userId);

    List<Rental> findByVideoId(Long videoId);
}
