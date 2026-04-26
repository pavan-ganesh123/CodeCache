package com.example.demo.controllers;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.security.core.Authentication;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepo;

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
}