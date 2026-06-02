package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.services.UserService;

public class UserResolver {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserService userservice;

    @QueryMapping
    public User getCurrentUser(Authentication auth) {

        if (auth == null) {
            throw new RuntimeException("User not authenticated");
        }

        String email = auth.getName();
        // System.out.println("AUTH NAME: " + auth.getName());
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @QueryMapping
    public List<User> getAllUsers(){
        return userservice.getAll();
    }
}
