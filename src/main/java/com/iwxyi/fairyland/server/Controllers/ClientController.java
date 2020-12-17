package com.iwxyi.fairyland.server.Controllers;

import java.util.Date;
import java.util.List;

import com.iwxyi.fairyland.server.Exception.GlobalResponse;
import com.iwxyi.fairyland.server.Models.ClientStartup;
import com.iwxyi.fairyland.server.Models.ClientUpdate;
import com.iwxyi.fairyland.server.Services.ClientStartupService;
import com.iwxyi.fairyland.server.Services.ClientUpdateService;

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
@RequestMapping(value = "/server/client", produces = "application/json;charset=UTF-8")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class ClientController {
    @Autowired
    ClientStartupService clientStartupService;
    @Autowired
    ClientUpdateService clientUpdateService;

    /**
     * 启动时发送本次启动以及之前的启动关闭
     */
    @PostMapping(value = "/startup")
    @ResponseBody
    public GlobalResponse<?> recordStartup(@RequestParam(value = "cpuId", required = false) String cpuId,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "startupTime") Long startupTime) {
        // #保存当前次的启动时间
        ClientStartup clientStartup = clientStartupService.saveStartup(cpuId, userId, new Date(startupTime));
        return GlobalResponse.map("startupId", clientStartup.getStartupId());
    }

    /**
     * 带有多条启动关闭的数据
     */
    @PostMapping(value = "/startupAndClose")
    @ResponseBody
    public GlobalResponse<?> recordStartupAndClose(@RequestBody List<ClientStartup> clientStartups) {
        // #保存之前上传的启动时间对应的关闭时间
        // (最后一项（如果有）必定是带有上次startupId的关闭时间)
        clientStartupService.saveStartupAndClose(clientStartups);
        return GlobalResponse.success();
    }

    @RequestMapping(value = "/updatedVersions")
    @ResponseBody
    public GlobalResponse<?> updatedVersions(@RequestParam(value = "app", required = false) String app,
            @RequestParam(value = "platform", required = false) String platform,
            @RequestParam(value = "version", required = false) Integer version) {
        List<ClientUpdate> clientUpdates = clientUpdateService.getUpdatedVersions(app, platform, version);
        return GlobalResponse.success(clientUpdates);
    }
}
