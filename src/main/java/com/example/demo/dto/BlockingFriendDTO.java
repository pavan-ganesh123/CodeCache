package com.example.demo.dto;

import com.example.demo.model.Friend;

public record BlockingFriendDTO(
        Long id,
        String status,
        BlockingUserDTO user,
        BlockingUserDTO friend
) {
    // IMPORTANT: call this from inside the @Transactional service
    // method, while `friend.getUser()`/`friend.getFriend()` can still
    // be initialized. Calling it later (e.g. back in the controller,
    // or after the method returns) defeats the point — the whole fix
    // is that the lazy proxies get resolved to plain fields before the
    // session that owns them closes.
    public static BlockingFriendDTO from(Friend relation) {
        return new BlockingFriendDTO(
                relation.getId(),
                relation.getStatus() != null ? relation.getStatus().name() : null,
                BlockingUserDTO.from(relation.getUser()),
                BlockingUserDTO.from(relation.getFriend())
        );
    }
}