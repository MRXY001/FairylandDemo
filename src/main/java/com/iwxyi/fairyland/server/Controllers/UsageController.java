package com.iwxyi.fairyland.server.Controllers;

import java.util.Date;
import java.util.List;

import com.iwxyi.fairyland.server.Exception.GlobalResponse;
import com.iwxyi.fairyland.server.Models.ClientStartup;
import com.iwxyi.fairyland.server.Services.ClientStartupService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
    @ResponseBody
    public GlobalResponse<?> recordStartup(@RequestParam(value = "cpuId", required = false) String cpuId,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "startupTime") Long startupTime) {
        // #保存当前次的启动时间
        ClientStartup clientStartup = clientStartupService.saveStartup(cpuId, userId, new Date(startupTime));
        return GlobalResponse.map("startupId", clientStartup.getStartupId());
    }

    @PostMapping(value = "/startupAndClose")
    @ResponseBody
    public GlobalResponse<?> recordStartupAndClose(@RequestBody List<ClientStartup> clientStartups) {
        // #保存之前上传的启动时间对应的关闭时间
        // (最后一项（如果有）必定是带有上次startupId的关闭时间)
        clientStartupService.saveStartupAndClose(clientStartups);
        return GlobalResponse.success();
    }
}
