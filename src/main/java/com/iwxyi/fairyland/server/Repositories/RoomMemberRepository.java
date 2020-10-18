package com.iwxyi.fairyland.server.Repositories;

import java.util.List;

import javax.transaction.Transactional;

import com.iwxyi.fairyland.server.Models.RoomMember;
import com.iwxyi.fairyland.server.Models.RoomMemberInfo;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface RoomMemberRepository extends CrudRepository<RoomMember, Long> {
    List<RoomMember> findByRoomIdOrderByContributionDesc(Long roomId);

    List<RoomMember> findByUserIdOrderByContribution(Long userId);

    RoomMember findByRoomIdAndUserId(Long roomId, Long userId);
    
    @Modifying
    @Transactional
    @Query("update RoomMember rm set rm.contribution = rm.contribution + :add where rm.userId = :userId")
    void increaseRoomMemberContribution(@Param("userId") Long userId, @Param("add") int add);

    // !这里要使用完整的包名
    @Query(value = "SELECT new com.iwxyi.fairyland.server.Models.RoomMemberInfo(u, m) FROM User u, RoomMember m WHERE m.roomId = :roomId AND u.userId = m.userId ORDER BY m.contribution DESC")
    public List<RoomMemberInfo> findRoomMemberInfo(@Param("roomId") Long roomId);
    
}
