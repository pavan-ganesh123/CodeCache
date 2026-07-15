package com.example.demo.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.FriendsChatDTO;
import com.example.demo.dto.UserSummaryDTO;
import com.example.demo.model.Friend;
import com.example.demo.model.Problem;
import com.example.demo.model.User;
import com.example.demo.model.enums.FriendStatus;
import com.example.demo.repository.FriendRepository;
import com.example.demo.repository.UserProblemRepository;
import com.example.demo.repository.UserRepository;

@Service
public class FriendService {
    private final FriendRepository friendRepo;
    private final UserRepository userRepo;

    @Autowired
    private UserProblemRepository userProblemRepository;
    public FriendService(FriendRepository friendRepo, UserRepository userRepo){
        this.friendRepo=friendRepo;
        this.userRepo=userRepo;
    }

    public Friend sendRequest(Long userId, Long friendId){
        if (userId.equals(friendId)) {
            throw new RuntimeErrorException(null, "Cannot Send request to yourself");
        }

        Optional<Friend> existing = friendRepo.findByUserIdAndFriendId(
            userId, friendId);
        
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
        // Friends are 2 mutual
        Friend p = new Friend();
        p.setUser(f.getFriend());
        p.setFriend(f.getUser());
        f.setStatus(FriendStatus.ACCEPTED);
        p.setStatus(FriendStatus.ACCEPTED);
        friendRepo.save(p);
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
System.out.println(relation.getUser().getUserName()+ "--"+ relation.getFriend().getUserName() + "Blocked");
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
        
        System.out.println(relation.getUser().getUserName()+ "--"+ relation.getFriend().getUserName());
        if (relation.getStatus() != FriendStatus.BLOCKED) {
            throw new RuntimeException("User is not blocked");
        }

        relation.setStatus(FriendStatus.ACCEPTED);

        return friendRepo.save(relation);
    }

    public List<Long> getFriendIds(Long userId) {
        Set<Long> ids = new HashSet<>();
        
        List<Friend> sent = friendRepo.findByUserIdAndStatus(userId, FriendStatus.ACCEPTED);
        List<Friend> received = friendRepo.findByFriendIdAndStatus(userId, FriendStatus.ACCEPTED);

        for(Friend f: sent){
            ids.add(f.getFriend().getId());
        }

        for(Friend f: received){
            ids.add(f.getUser().getId());
        }
        return new ArrayList<>(ids);
    }
    public List<Friend> getBlockedUsers(Long userId){
        return friendRepo.findByUserIdAndStatus(userId, FriendStatus.BLOCKED);
    }
    public List<Friend> getPendingFriends(Long userId){
        return friendRepo.findByFriendIdAndStatus(userId, FriendStatus.PENDING);
    }
    @Transactional(readOnly = true)
    public List<FriendsChatDTO> getAllRelations(Long userId) {
        List<Friend> friends= friendRepo.findAllRelations(userId);
        return friends.stream()
                .map(f -> tofriendDTO(f,userId))
                .toList();
    }

    public boolean areUsersFriends(Long userId, Long targetUserId) {
        return friendRepo.findRelation(userId, targetUserId)
            .map(friend -> friend.getStatus() == FriendStatus.ACCEPTED)
            .orElse(false);
    }
    public List<Problem> getFriendsSolvedProblems(Long userId) {
        // Get all accepted friend IDs (bidirectional: user is either user_id or friend_id)
        List<Long> friendIds = friendRepo.getAcceptedFriendIds(userId, FriendStatus.ACCEPTED);
        
        if (friendIds.isEmpty()) {
            return List.of();
        }
        
        // Get problems solved by those friend IDs
        return userProblemRepository.findSolvedProblemsByFriendIds(friendIds);
    }

    public FriendsChatDTO tofriendDTO(Friend f, Long userId){
        FriendsChatDTO fDTO  = new FriendsChatDTO();
        fDTO.setId(f.getId());
        fDTO.setStatus(f.getStatus());
        if(f.getUser().getId() == userId){
            UserSummaryDTO userDto = new UserSummaryDTO();
            userDto.setId(f.getUser().getId());
            userDto.setUserName(f.getUser().getUserName());
            userDto.setProfilePicture(f.getUser().getProfilePicture());

            UserSummaryDTO friendDto = new UserSummaryDTO();
            friendDto.setId(f.getFriend().getId());
            friendDto.setUserName(f.getFriend().getUserName());
            friendDto.setProfilePicture(f.getFriend().getProfilePicture());

            fDTO.setUser(userDto);
            fDTO.setFriend(friendDto);
            fDTO.setProfileImage(f.getFriend().getProfilePicture());
        }
        else {
            UserSummaryDTO userDto = new UserSummaryDTO();
            userDto.setId(f.getUser().getId());
            userDto.setUserName(f.getUser().getUserName());
            userDto.setProfilePicture(f.getUser().getProfilePicture());

            UserSummaryDTO friendDto = new UserSummaryDTO();
            friendDto.setId(f.getFriend().getId());
            friendDto.setUserName(f.getFriend().getUserName());
            friendDto.setProfilePicture(f.getFriend().getProfilePicture());

            fDTO.setUser(userDto);
            fDTO.setFriend(friendDto);
            fDTO.setProfileImage(f.getUser().getProfilePicture());
        }
        return fDTO;
    }
}
