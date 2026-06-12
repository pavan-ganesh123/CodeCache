package com.example.demo.controllers;

import com.example.demo.dto.ProfileResponse;
import com.example.demo.model.User;
import com.example.demo.model.UserStats;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.SecurityUtil;
import com.example.demo.services.ProblemService;
import com.example.demo.services.UserService;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ProblemService problemservice;
    @Autowired
    private SecurityUtil securityUtil;
    
    @Autowired
    private UserService uservice;
    
    @GetMapping("/me")
    public User getCurrentUser(Authentication auth) {

        if (auth == null) {
            throw new RuntimeException("User not authenticated");
        }

        String email = auth.getName();
        // System.out.println("AUTH NAME: " + auth.getName());
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile() {

        Long userId = securityUtil.getCurrentUserId();

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ProfileResponse response =
                new ProfileResponse(
                        user.getId(),
                        user.getUserName(),
                        user.getEmail(),
                        user.getProfilePicture()
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(
        @RequestParam String query,
        @RequestHeader("Authorization") String token) {
        
        // Exclude current user and already-friends/pending users
        Long userId = securityUtil.getCurrentUserId();
        List<User> users = uservice.searchUsersByUsername(query,userId);
        return ResponseEntity.ok(users);
    }
    @GetMapping("/countFriends")
    public ResponseEntity<Long> getFriendNumber() {

        Long userId = securityUtil.getCurrentUserId();

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(0L);
        }

        return ResponseEntity.ok(problemservice.countFriends(userId));
    }


    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        System.out.println("✓ getAllUsers API HIT!");
        
        List<User> users = userRepo.findAll();
        System.out.println("✓ Returned " + (users != null ? users.size() : "null") + " users");
        
        if (users == null) {
            return ResponseEntity.ok(java.util.Collections.emptyList());
        }
        
        return ResponseEntity.ok(users);
    }

    @PostMapping("/my-profile-picture")
    public ResponseEntity<?> uploadProfilePicture(
            @RequestBody Map<String, String> body,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String base64Image = body.get("profilePicture");

            if (base64Image == null || base64Image.isEmpty()) {
                return ResponseEntity.badRequest().body("Image data is required");
            }

            Long userId = securityUtil.getCurrentUserId();
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setProfilePicture(base64Image);
            userRepo.save(user);

            return ResponseEntity.ok("Profile picture updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update profile picture");
        }
    }

    // Get YOUR own profile picture
    @GetMapping("/my-profile-picture")
    public ResponseEntity<?> getMyProfilePicture() {
        try {
            Long userId = securityUtil.getCurrentUserId();
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.getProfilePicture() == null || user.getProfilePicture().isEmpty()) {
                return ResponseEntity.noContent().build(); // 204 - no picture set
            }

            return ResponseEntity.ok(Map.of("profilePicture", user.getProfilePicture()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch profile picture");
        }
    }

    @GetMapping("/{userId}/profile-picture")
    public ResponseEntity<?> getUserProfilePicture(@PathVariable Long userId) {
        try {
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.getProfilePicture() == null || user.getProfilePicture().isEmpty()) {
                return ResponseEntity.noContent().build(); // 204 - no picture set
            }

            return ResponseEntity.ok(Map.of("profilePicture", user.getProfilePicture()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch profile picture");
        }
    }

    @GetMapping("my-stats")
    public ResponseEntity<UserStats> getUserStats() {
        Long userId = securityUtil.getCurrentUserId();
        return ResponseEntity.ok(uservice.getUserStats(userId)); 
    }

    @GetMapping("/my-yearly-submissions")
    public ResponseEntity<Map<String, Integer>> getYearlySubmissions() {
        Long userId = securityUtil.getCurrentUserId();
        return ResponseEntity.ok(problemservice.getYearlySubmissions(userId));
    }
    
    
}