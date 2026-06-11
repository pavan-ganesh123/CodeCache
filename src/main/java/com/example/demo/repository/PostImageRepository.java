package com.example.demo.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

import com.example.demo.model.PostImage;

import java.util.*;


public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    List<PostImage> findByPostId(Long postId);

    List<PostImage> findByPostIdAndStatus(Long postId, String status);

    Optional<PostImage> findFirstByPostIdAndIsPrimaryTrue(Long postId);
}