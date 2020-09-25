package com.iwxyi.fairyland.server.Controllers;

import java.util.Date;

import com.iwxyi.fairyland.server.Exception.GlobalResponse;
import com.iwxyi.fairyland.server.Services.ClientStartupService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 记录客户端的使用数据，包括但不限于：
 * - 启动记录
 * - 用户反馈（需要登录）
 * - 小黑屋
 */

@RestController
@RequestMapping(value = "/server/usage", produces = "application/json;charset=UTF-8")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class UsageController {
    @Autowired
    ClientStartupService clientStartupService;

    @PostMapping(value = "/startup")
    public GlobalResponse<?> recordStartup(@RequestParam(value = "cpuId", required = false) String cpuId,
            @RequestParam(value = "userId", required = false) Long userId, @RequestParam(value = "startupTime") Long startupTime) {
            // #保存之前上传的启动时间对应的关闭时间
            // #保存中间的启动关闭时间对
            // #保存当前次的启动时间
            clientStartupService.saveStartup(cpuId, userId, new Date(startupTime));
        return GlobalResponse.success();
    }
}
