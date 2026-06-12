package com.example.demo.services;

import java.time.LocalDate;
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
import com.example.demo.model.UserLogin;
import com.example.demo.model.UserStats;
import com.example.demo.model.enums.FriendStatus;
import com.example.demo.repository.FriendRepository;
import com.example.demo.repository.UserLoginRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UserStatsRepository;
import com.example.demo.repository.UserSubmissionRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.security.SecurityUtil;

@Service
public class UserService {
    @Autowired
    private UserRepository repo;

    @Autowired
    private FriendRepository fRepo;

    @Autowired
    private UserSubmissionRepository submissionRepo;

    @Autowired
    private UserStatsRepository statsRepo;

    @Autowired
    private UserLoginRepository loginRepo;

    @Autowired
    private SecurityUtil securityUtil;

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
        trackLogin(user.getId());
        System.out.println("LOGIN SUCCESS");

        return JwtUtil.generateToken(user.getId(),email);
    }

    public void trackLogin(Long userId) {
        LocalDate today  = LocalDate.now();
        UserLogin alreadyLogged = loginRepo.findByUserIdAndLoginDate(userId, today);
        if (alreadyLogged != null) return;
        
        UserLogin ul= new UserLogin();
        System.out.println("Loginned in User to track "+ userId);
        ul.setUserId(userId);
        ul.setLoginDate(today);
        loginRepo.save(ul);
        
        UserStats st = statsRepo.findByUserId(userId);
        if(st == null) {
            st = new UserStats();
            st.setUserId(userId);
        }
        LocalDate yesterday = today.minusDays(1);
        UserLogin yesterdayLogin = loginRepo.findByUserIdAndLoginDate(userId, yesterday);

        if (yesterdayLogin != null ){
            st.setCurrentStreak(st.getCurrentStreak() + 1);
        } else {
            st.setCurrentStreak(1);
        }
        if (st.getCurrentStreak() > st.getLongestStreak()){
            st.setLongestStreak(st.getCurrentStreak());
        }

        st.setTotalPoints(st.getTotalPoints() + 10);
        st.setLastLoginDate(today);
        statsRepo.save(st);
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

    public UserStats getUserStats(Long userId) {
        
        UserStats uStats = statsRepo.findByUserId(userId);
        if (uStats ==null){
            uStats = new UserStats();
            uStats.setUserId(userId);
            uStats.setTotalPoints(0);
            uStats.setCurrentStreak(0);
            uStats.setLongestStreak(0);
        }
        return uStats;
    }
}
