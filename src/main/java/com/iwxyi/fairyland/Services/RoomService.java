package com.iwxyi.fairyland.Services;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import com.iwxyi.fairyland.Config.ErrorCode;
import com.iwxyi.fairyland.Exception.FormatedException;
import com.iwxyi.fairyland.Models.Room;
import com.iwxyi.fairyland.Models.RoomHistory;
import com.iwxyi.fairyland.Models.RoomMember;
import com.iwxyi.fairyland.Models.User;
import com.iwxyi.fairyland.Repositories.RoomHistoryRepository;
import com.iwxyi.fairyland.Repositories.RoomMemberRepository;
import com.iwxyi.fairyland.Repositories.RoomRepository;
import com.iwxyi.fairyland.Repositories.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class RoomService {
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoomMemberRepository roomMemberRepository;
    @Autowired
    RoomHistoryRepository roomHistoryRepository;

    Logger logger = LoggerFactory.getLogger(RoomService.class);

    public Room createRoom(User user, String roomName, String password, String introduction) {
        // 判断用户能否再加入房间
        canUserJoinRoom(user);
        // 创建房间
        Room room = new Room(user.getUserId(), roomName, password, introduction);
        room = roomRepository.save(room);
        // 用户加入自己创建的房间
        room = joinRoom(user, room);
        return room;
    }

    public Room joinRoom(User user, Long roomId) {
        return joinRoom(user, roomRepository.findByRoomId(roomId));
    }

    public Room joinRoom(User user, Room room) {
        canUserJoinRoom(user);

        // 房间成员数量+1
        room.setMemberCount(room.getMemberCount() + 1);
        room = roomRepository.save(room);

        // 保存用户的房间
        RoomMember roomMember = new RoomMember(room.getRoomId(), user.getUserId(), new Date());
        roomMemberRepository.save(roomMember);

        // 保存用户加入房间的历史
        RoomHistory roomHistory = new RoomHistory(room.getRoomId(), user.getUserId(), new Date());
        roomHistoryRepository.save(roomHistory);

        // 保存用户加入房间的个数
        user.setRoomJoinedCount(user.getRoomJoinedCount() + 1);
        userRepository.save(user);

        return room;
    }

    public void leaveRoom(User user, Long roomId) {
        leaveRoom(user, roomRepository.findByRoomId(roomId));
    }

    public void leaveRoom(User user, Room room) {
        // 判断是否确实是加入了
        RoomMember roomMember = roomMemberRepository.findByRoomIdAndUserId(room.getRoomId(), user.getUserId());
        if (roomMember == null) {
            throw new FormatedException("您未加入该房间", ErrorCode.NotExist);
        }

        // 房间成员数-1
        room.setMemberCount(room.getMemberCount() - 1);
        roomRepository.save(room);

        // 用户离开房间
        int integral = roomMember.getIntegral(); // 这一时刻的积分
        roomMemberRepository.delete(roomMember);

        // 保存离开房间的历史
        RoomHistory roomHistory = roomHistoryRepository.findFirstByRoomIdAndUserId(room.getRoomId(), user.getUserId());
        if (roomHistory == null) {
            roomHistory = new RoomHistory(room.getRoomId(), user.getUserId(), new Date(), integral);
        } else {
            roomHistory.leave(new Date(), integral);
        }
        roomHistoryRepository.save(roomHistory);
        
        // 用户加入的房间数-1
        user.setRoomJoinedCount(user.getRoomJoinedCount() - 1);
        userRepository.save(user);
    }

    /**
     * 解散房间
     * 需要房主才能操作
     */
    public void disbandRoom(Long userId, Long roomId) {
        Room room = roomRepository.findByRoomId(roomId);
        if (room == null) {
            throw new FormatedException("未找到该房间", ErrorCode.NotExist);
        }
        if (room.getOwnerId() != userId) {
            throw new FormatedException("只有房主才能解散拼字房间", ErrorCode.Permission);
        }

        // 移除房间用户
        List<RoomMember> members = roomMemberRepository.findByRoomId(roomId);
        for (int i = 0; i < members.size(); i++) {
            RoomMember member = members.get(i);

            // 保存退出时的记录
            RoomHistory history = roomHistoryRepository.findFirstByRoomIdAndUserId(roomId, member.getUserId());
            if (history != null) {
                history.setLeaveIntegral(member.getIntegral());
                roomHistoryRepository.save(history);
            }

            // 用户加入的房间数量-1
            User user = userRepository.findByUserId(member.getUserId());
            user.setRoomJoinedCount(user.getRoomJoinedCount() - 1);
            userRepository.save(user);

            // 彻底移除成员
            roomMemberRepository.delete(member);
        }

        // 开始解散房间（标记为deleted）
        room.setMemberCount(0);
        room.setDeleted(true);
        roomRepository.save(room);
    }

    public Page<Room> pagedRank(int page, int size, Sort sort) {
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Room> rooms = roomRepository.findByDeletedFalse(pageable);
        return rooms;
    }

    private boolean canUserJoinRoom(User user) {
        int maxCount = user.getRoomMaxCount();
        // 其实还有个 user.getRoomJoinedCount()，但这只是标记，不用来做判断依据
        int count = (roomMemberRepository.findByUserId(user.getUserId())).size();
        if (count >= maxCount) {
            throw new FormatedException("您已加入" + count + "个房间，达到上限，请先退出已有房间", ErrorCode.Insufficient);
        }
        return true;
    }
}
