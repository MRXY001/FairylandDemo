package com.iwxyi.fairyland.Repositories;

import com.iwxyi.fairyland.Models.RoomHistory;

import org.springframework.data.repository.CrudRepository;

public interface RoomHistoryRepository extends CrudRepository<RoomHistory, Long> {
    RoomHistory findFirstByRoomIdAndUserId(Long roomId, Long userId);
    
}
