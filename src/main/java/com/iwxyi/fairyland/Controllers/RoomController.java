package com.iwxyi.fairyland.Controllers;

import com.iwxyi.fairyland.Authentication.LoginUser;
import com.iwxyi.fairyland.Exception.GlobalResponse;
import com.iwxyi.fairyland.Models.Room;
import com.iwxyi.fairyland.Models.User;
import com.iwxyi.fairyland.Services.RoomService;
import com.iwxyi.fairyland.Services.UserService;

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
@RequestMapping(value = "/room", produces = "application/json;charset=UTF-8")
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
    public GlobalResponse<?> joinRoom(@LoginUser User user, @RequestParam("roomId") Long roomId) {
        Room room = roomService.joinRoom(user, roomId);
        return GlobalResponse.success(room);
    }

    /**
     * 离开房间
     */
    @PostMapping(value = "/leave")
    public GlobalResponse<?> leaveRoom(@LoginUser User user) {
        roomService.leaveRoom(user);
        return GlobalResponse.success();
    }

    @PostMapping(value = "/disband")
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
}
