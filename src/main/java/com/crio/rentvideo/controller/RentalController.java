package com.crio.rentvideo.controller;

import com.crio.rentvideo.dto.RentalResponse;
import com.crio.rentvideo.entity.Role;
import com.crio.rentvideo.entity.User;
import com.crio.rentvideo.service.RentalService;
import com.crio.rentvideo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RentalController {

    private final RentalService rentalService;
    private final UserService userService;

    @Autowired
    public RentalController(RentalService rentalService, UserService userService) {
        this.rentalService = rentalService;
        this.userService = userService;
    }

    @PostMapping({"/videos/{videoId}/rent", "/api/videos/{videoId}/rent", "/books/{videoId}/rent", "/api/books/{videoId}/rent"})
    public ResponseEntity<RentalResponse> rentVideo(@PathVariable Long videoId, Authentication authentication) {
        String email = authentication.getName();
        RentalResponse response = rentalService.rentVideo(videoId, email);
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/videos/{videoId}/return", "/api/videos/{videoId}/return", "/books/{videoId}/return", "/api/books/{videoId}/return"})
    public ResponseEntity<RentalResponse> returnVideo(@PathVariable Long videoId, Authentication authentication) {
        String email = authentication.getName();
        RentalResponse response = rentalService.returnVideo(videoId, email);
        return ResponseEntity.ok(response);
    }

    @GetMapping({"/rentals", "/api/rentals"})
    public ResponseEntity<List<RentalResponse>> getRentals(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        if (user.getRole() == Role.ADMIN) {
            return ResponseEntity.ok(rentalService.getAllRentals());
        }
        return ResponseEntity.ok(rentalService.getUserRentals(email));
    }

    @GetMapping({"/rentals/my", "/api/rentals/my"})
    public ResponseEntity<List<RentalResponse>> getMyRentals(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(rentalService.getUserRentals(email));
    }
}
