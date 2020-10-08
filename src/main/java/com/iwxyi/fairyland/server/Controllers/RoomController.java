package com.iwxyi.fairyland.server.Controllers;

import java.util.List;

import com.iwxyi.fairyland.server.Authentication.LoginRequired;
import com.iwxyi.fairyland.server.Authentication.LoginUser;
import com.iwxyi.fairyland.server.Exception.GlobalResponse;
import com.iwxyi.fairyland.server.Models.Room;
import com.iwxyi.fairyland.server.Models.RoomMember;
import com.iwxyi.fairyland.server.Models.User;
import com.iwxyi.fairyland.server.Services.RoomService;
import com.iwxyi.fairyland.server.Services.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/server/room", produces = "application/json;charset=UTF-8")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*") // 解决跨域问题
public class RoomController {
    @Autowired
    UserService userService;
    @Autowired
    RoomService roomService;

    Logger logger = LoggerFactory.getLogger(RoomService.class);

    /**
     * 创建一个房间
     * *如果用户已经有房间的话，则会创建失败
     */
    @PostMapping(value = "/create")
    @LoginRequired
    public GlobalResponse<?> createRoom(@LoginUser User user, @RequestParam("roomName") String roomName,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "introduction", required = false) String introduction) {
        Room room = roomService.createRoom(user, roomName, password, introduction);
        return GlobalResponse.success(room);
    }

    /**
     * 加入房间
     * *如果用户已经加入了其他房间，则会加入失败
     */
    @PostMapping(value = "/join")
    @LoginRequired
    public GlobalResponse<?> joinRoom(@LoginUser User user, @RequestParam("roomId") Long roomId,
            @RequestParam(value = "password", required = false) String password) {
        Room room = roomService.joinRoom(user, roomId, password);
        return GlobalResponse.success(room);
    }

    /**
     * 离开房间
     */
    @PostMapping(value = "/leave")
    @LoginRequired
    public GlobalResponse<?> leaveRoom(@LoginUser User user, @RequestParam("roomId") Long roomId) {
        roomService.leaveRoom(user, roomId);
        return GlobalResponse.success();
    }

    @PostMapping(value = "/disband")
    @LoginRequired
    public GlobalResponse<?> disbandRoom(@LoginUser Long userId, @RequestParam("roomId") Long roomId) {
        roomService.disbandRoom(userId, roomId);
        return GlobalResponse.success();
    }

    @PostMapping(value = "/rank")
    public GlobalResponse<?> roomRank(@RequestParam(value = "pageNumber", required = false) Integer pageNumber) {
        if (pageNumber == null) {
            pageNumber = 1;
        }

        // 排序方式：等级
        Page<Room> rooms = roomService.pagedRank(pageNumber, 20, Sort.by(Sort.Direction.DESC, "level"));
        return GlobalResponse.success(rooms);
    }

    @PostMapping(value = "/myRooms")
    @LoginRequired
    public GlobalResponse<?> myRooms(@LoginUser Long userId) {
        List<Room> rooms = roomService.getUserRooms(userId);
        return GlobalResponse.success(rooms);
    }
    
    @PostMapping(value = "/roomMembers")
    public GlobalResponse<?> roomMembers(Long roomId) {
        List<RoomMember> members = roomService.getRoomMembers(roomId);
        return GlobalResponse.success(members);
    }
}
