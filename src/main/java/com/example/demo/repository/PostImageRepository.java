package com.example.demo.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.*;

import com.example.demo.model.PostImage;

import java.util.*;


public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    @Query("SELECT pi FROM PostImage pi JOIN FETCH pi.post WHERE pi.post.id = :postId")
    List<PostImage> findByPostId(@Param("postId") Long postId);

    List<PostImage> findByPostIdAndStatus(Long postId, String status);

    Optional<PostImage> findFirstByPostIdAndIsPrimaryTrue(Long postId);
    List<PostImage> findByPostIdInAndIsPrimaryTrue(List<Long> postIds);
}