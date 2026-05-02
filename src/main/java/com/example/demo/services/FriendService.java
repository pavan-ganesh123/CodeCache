package com.example.demo.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.management.RuntimeErrorException;

import org.springframework.stereotype.Service;

import com.example.demo.model.Friend;
import com.example.demo.model.User;
import com.example.demo.model.enums.FriendStatus;
import com.example.demo.repository.FriendRepository;
import com.example.demo.repository.UserRepository;

@Service
public class FriendService {
    private final FriendRepository friendRepo;
    private final UserRepository userRepo;

    public FriendService(FriendRepository friendRepo, UserRepository userRepo){
        this.friendRepo=friendRepo;
        this.userRepo=userRepo;
    }

    public Friend sendRequest(Long userId, Long friendId){
        if (userId.equals(friendId)) {
            throw new RuntimeErrorException(null, "Cannot Send request to yourself");
        }

        Optional<Friend> existing = friendRepo.findByUserIdAndFriendIdOrUserIdAndFriendId(
            userId, friendId, 
            friendId, userId);
        
        if(existing.isPresent()){
            throw new RuntimeErrorException(null,"Friend Request already Exists");
        }

        User user = userRepo.findById(userId).orElseThrow();
        User friend = userRepo.findById(friendId).orElseThrow();

        Friend f = new Friend();
        f.setUser(user);
        f.setFriend(friend);
        f.setStatus(FriendStatus.PENDING);

        return friendRepo.save(f);
    }

    public Friend acceptRequest(Long requestId){
        Friend f =friendRepo.findById(requestId).orElseThrow();

        f.setStatus(FriendStatus.ACCEPTED);
        return friendRepo.save(f);
    }

    public Friend blockUser(Long userId, Long targetUserId) {

        if (userId.equals(targetUserId)) {
            throw new RuntimeException("Cannot block yourself");
        }

        Optional<Friend> existing =
            friendRepo.findRelation(
                userId,
                targetUserId
            );

        Friend relation;

        if (existing.isPresent()) {
            relation = existing.get();
        } else {
            User user = userRepo.findById(userId).orElseThrow();
            User target = userRepo.findById(targetUserId).orElseThrow();

            relation = new Friend();
            relation.setUser(user);
            relation.setFriend(target);
        }

        relation.setStatus(FriendStatus.BLOCKED);

        return friendRepo.save(relation);
    }

    public Friend unblockUser(Long userId, Long targetUserId) {

        Friend relation = friendRepo.findRelation(
                userId,
                targetUserId
            )
            .orElseThrow(() ->
                new RuntimeException("Relation not found")
            );

        if (relation.getStatus() != FriendStatus.BLOCKED) {
            throw new RuntimeException("User is not blocked");
        }

        relation.setStatus(FriendStatus.ACCEPTED);

        return friendRepo.save(relation);
    }

    public List<Long> getFriendIds(Long userId) {
        List<Friend> sent = friendRepo.findByUserIdAndStatus(userId, FriendStatus.ACCEPTED);
        List<Friend> received = friendRepo.findByFriendIdAndStatus(userId, FriendStatus.ACCEPTED);

        List<Long> ids = new ArrayList<>();
        for(Friend f: sent){
            ids.add(f.getFriend().getId());
        }

        for(Friend f: received){
            ids.add(f.getUser().getId());
        }
        return ids;
    }

    public List<Friend> getAllRelations(Long userId) {
        return friendRepo.findAllRelations(userId);
    }
}
