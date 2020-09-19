package com.iwxyi.fairyland.server.Repositories;

import com.iwxyi.fairyland.server.Models.RoomHistory;

import org.springframework.data.repository.CrudRepository;

public interface RoomHistoryRepository extends CrudRepository<RoomHistory, Long> {
    RoomHistory findFirstByRoomIdAndUserId(Long roomId, Long userId);
    
}
