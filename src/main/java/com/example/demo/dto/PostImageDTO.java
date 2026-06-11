package com.example.demo.dto;


import java.time.LocalDateTime;

public class PostImageDTO {
    private Long id;
    private String imageUrl;
    private String caption;
    private Boolean isPrimary;
    private String status;
    private Long uploadedBy;
    private LocalDateTime createdAt;

    private Long postId;
    private String postQuestionTitle;
    private String postUserName;

    public PostImageDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }

    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(Long uploadedBy) { this.uploadedBy = uploadedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public String getPostQuestionTitle() { return postQuestionTitle; }
    public void setPostQuestionTitle(String postQuestionTitle) { this.postQuestionTitle = postQuestionTitle; }

    public String getPostUserName() { return postUserName; }
    public void setPostUserName(String postUserName) { this.postUserName = postUserName; }
}