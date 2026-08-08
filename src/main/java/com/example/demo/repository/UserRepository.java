package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.User;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findById(long id);
    Optional<User> findByUserName(String name);

    boolean existsByUserName(String userName);

    @Query("SELECT u FROM User u WHERE u.userName LIKE %:userName%")
    List<User> findByUserNameContaining(@Param("userName") String userName);
}
