package com.example.demo.controllers;

import com.example.demo.dto.BlockingFriendDTO;
import com.example.demo.dto.FriendsChatDTO;
import com.example.demo.model.Friend;
import com.example.demo.services.FriendService;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class FriendResolver {

    private final FriendService friendService;

    public FriendResolver(FriendService friendService) {
        this.friendService = friendService;
    }

    @MutationMapping
    public Friend sendFriendRequest(@Argument Long userId, @Argument Long friendId) {
        return friendService.sendRequest(userId, friendId);
    }

    @MutationMapping
    public Friend acceptFriendRequest(@Argument Long requestId) {
        return friendService.acceptRequest(requestId);
    }

    @MutationMapping
    public BlockingFriendDTO blockUser(@Argument Long userId, @Argument Long targetUserId) {
        return friendService.blockUser(userId, targetUserId);
    }

    @MutationMapping
    public BlockingFriendDTO unblockUser(@Argument Long userId, @Argument Long targetUserId){
        return friendService.unblockUser(userId, targetUserId);
    }
    @QueryMapping
    public List<Long> getFriends(@Argument Long userId) {
        return friendService.getFriendIds(userId);
    }
    @QueryMapping
    public List<FriendsChatDTO> getAllFriends(@Argument Long userId) {
        return friendService.getAllRelations(userId);
    }

    @QueryMapping
    public List<BlockingFriendDTO> getBlockedFriends(@Argument Long userId) {
        return friendService.getBlockedUsers(userId);
    }

    @QueryMapping
    public List<Friend> getPendingFriends(@Argument Long userId) {
        return friendService.getPendingFriends(userId);
    }
}