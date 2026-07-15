package com.example.demo.dto;

import com.example.demo.model.Friend;
import com.example.demo.model.User;
import com.example.demo.model.enums.FriendStatus;

public class FriendsChatDTO {
    private Long id;
    private FriendStatus status;
    private UserSummaryDTO friend;
    private UserSummaryDTO user; 
    private String profileImage;

    public FriendsChatDTO() {}
    public FriendsChatDTO(Long id, FriendStatus status , UserSummaryDTO user,UserSummaryDTO friend, String profileImage){
        this.id =id;
        this.status=status;
        this.friend = friend;
        this.user  =user;
        this.profileImage = profileImage;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public FriendStatus getStatus() {
        return status;
    }
    public void setStatus(FriendStatus status) {
        this.status = status;
    }
    public UserSummaryDTO getFriend() {
        return friend;
    }
    public void setFriend(UserSummaryDTO friend) {
        this.friend = friend;
    }
    public String getProfileImage() {
        return profileImage;
    }
    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
    public UserSummaryDTO getUser() {
        return user;
    }
    public void setUser(UserSummaryDTO user) {
        this.user = user;
    }
    
}
