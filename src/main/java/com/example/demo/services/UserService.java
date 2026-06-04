package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;

@Service
public class UserService {
    @Autowired
    private UserRepository repo;
    
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

}
