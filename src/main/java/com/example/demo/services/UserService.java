package com.example.demo.services;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Friend;
import com.example.demo.model.User;
import com.example.demo.model.enums.FriendStatus;
import com.example.demo.repository.FriendRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;

@Service
public class UserService {
    @Autowired
    private UserRepository repo;

    @Autowired
    private FriendRepository fRepo;

    public User save(User p){
        return repo.save(p);
    }
    public List<User> getAll(){
        return repo.findAll();
    }
    public String login(String email, String password) {
        System.out.println("LOGIN API HIT");

        email = email.trim();
        password = password.trim();

        User user = repo.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("User NOT FOUND");
                    return new RuntimeException("User not found");
                });


        if (!user.getPassword().equals(password)) {
            System.out.println("PASSWORD MISMATCH");
            throw new RuntimeException("Invalid password");
        }

        System.out.println("LOGIN SUCCESS");

        return JwtUtil.generateToken(user.getId(),email);
    }

    public boolean existsByUserName(String userName) {
        return repo.existsByUserName(userName);
    }

    public List<User> searchUsersByUsername(String query, Long currentUserId) {
        // Get all users matching username
        List<User> matchingUsers = repo.findByUserNameContaining(query);
        
        // Get all friend relations for current user
        Set<Long> excludedIds = fRepo.getExcludedFriendIds(currentUserId);
    excludedIds.add(currentUserId);
        
        
        // Filter out excluded users
        return matchingUsers.stream()
            .filter(user -> !excludedIds.contains(user.getId()))
            .collect(Collectors.toList());
    }
}
