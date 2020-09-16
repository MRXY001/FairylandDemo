package com.iwxyi.fairyland.Services;

import java.util.Date;

import com.iwxyi.fairyland.Config.ConstantValue;
import com.iwxyi.fairyland.Config.ErrorCode;
import com.iwxyi.fairyland.Exception.FormatedException;
import com.iwxyi.fairyland.Models.Room;
import com.iwxyi.fairyland.Models.User;
import com.iwxyi.fairyland.Repositories.RoomRepository;
import com.iwxyi.fairyland.Repositories.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomService {
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    UserRepository userRepository;

    Logger logger = LoggerFactory.getLogger(RoomService.class);

    public Room createRoom(User user, String roomName, String password, String introduction) {
        if (user.getRoomId() != null) {
            throw new FormatedException("您已加入房间，请离开房间后再创建", ErrorCode.Exist);
        }
        // 创建房间
        Room room = new Room(user.getUserId(), roomName, password, introduction);
        room = roomRepository.save(room);
        // 用户加入自己创建的房间
        room = joinRoom(user, room);
        return room;
    }

    public Room joinRoom(User user, Room room) {
        if (user.getRoomId() != null) {
            Room rm = roomRepository.findByRoomId(user.getRoomId());
            // 有一种情况，就是用户房间删除了，不能创建
            if (rm != null) {
                throw new FormatedException("您已加入房间：" + rm.getRoomName() + "，不可重复加入", ErrorCode.Exist);
            }
        }
        // 房间成员数量+1
        room.setMemberCount(room.getMemberCount() + 1);
        room = roomRepository.save(room);
        // 保存用户的房间
        user.setRoomId(room.getRoomId());
        user.setJoinRoomTime(new Date());
        userRepository.save(user);
        return room;
    }

    public void leaveRoom(User user) {
        if (user.getRoomId() == null) {
            throw new FormatedException("您未加入房间", ErrorCode.NotExist);
        }
        if (user.getJoinRoomTime().getTime() + ConstantValue.ROOM_LEAVE_INTERVAL > (new Date()).getTime()) {
            throw new FormatedException("加入房间后需要12小时才能退出", ErrorCode.Wait);
        }
        Room room = roomRepository.findByRoomId(user.getRoomId());
        if (room != null) {
            // 退出房间，成员数量-1
            room.setMemberCount(room.getMemberCount() - 1);
            roomRepository.save(room);
        }
        user.setRoomId(null);
        userRepository.save(user);
    }
}
