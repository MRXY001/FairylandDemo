package com.iwxyi.fairyland.server.Repositories;

import java.util.List;

import javax.transaction.Transactional;

import com.iwxyi.fairyland.server.Models.Room;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface RoomRepository extends CrudRepository<Room, Long> {
    Room findByRoomId(Long roomId);
    
    List<Room> findByOwnerIdAndDeletedFalse(Long ownerId);

    Page<Room> findByDeletedFalse(Pageable pageable);

    @Modifying
    @Transactional
    @Query("update Room r set r.ownerName = :nickname where r.ownerId = :ownerId")
    void modifyOwnerNickname(Long ownerId, String nickname);

}
