package com.iwxyi.fairyland.Repositories;

import java.util.List;

import javax.transaction.Transactional;

import com.iwxyi.fairyland.Models.RoomMember;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface RoomMemberRepository extends CrudRepository<RoomMember, Long> {
    List<RoomMember> findByRoomId(Long roomId);

    List<RoomMember> findByUserId(Long userId);

    RoomMember findByRoomIdAndUserId(Long roomId, Long userId);
    
    @Modifying
    @Transactional
    @Query("update room_member set intergral = integral + :add where user_id = :userId")
    void increaseRoomMemberIntegral(Long userId, int add);

}
