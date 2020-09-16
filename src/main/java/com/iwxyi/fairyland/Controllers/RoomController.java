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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
    
    

    @PostMapping(value = "/rank")
    @ResponseBody
    public GlobalResponse<?> rank() {
        return GlobalResponse.success();
    }
}
