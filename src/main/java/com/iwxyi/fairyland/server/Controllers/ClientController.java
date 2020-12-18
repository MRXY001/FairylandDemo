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
     * 启动时保存运行时间
     */
    @PostMapping(value = "/recordStartup")
    @ResponseBody
    private GlobalResponse<?> recordStartup(@RequestParam(value = "cpuId", required = false) String cpuId,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "pair", required = false) String pairsStr) {
        // #保存当前次的启动时间
        clientStartupService.saveValues(cpuId, userId, pairsStr);
        return GlobalResponse.success();
    }

    /**
     * 检测程序更新的版本
     */
    @RequestMapping(value = "/updatedVersions")
    @ResponseBody
    private GlobalResponse<?> updatedVersions(@RequestParam(value = "app", required = false) String app,
            @RequestParam(value = "platform", required = false) String platform,
            @RequestParam(value = "version", required = false) Integer version) {
        // #获取更新的版本
        List<ClientUpdate> clientUpdates = clientUpdateService.getUpdatedVersions(app, platform, version);
        return GlobalResponse.success(clientUpdates);
    }

    /**
     * 所有启动都在上面
     */
    @RequestMapping(value = "/clientStartup")
    @ResponseBody
    public GlobalResponse<?> clientStartup(@RequestParam(value = "app", required = false) String app,
            @RequestParam(value = "platform", required = false) String platform,
            @RequestParam(value = "version", required = false) Integer version,
            @RequestParam(value = "cpuId", required = false) String cpuId,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "pair", required = false) String pairsStr) {
        // #保存当前次的启动时间
        clientStartupService.saveValues(cpuId, userId, pairsStr);
        // #获取更新的版本
        List<ClientUpdate> clientUpdates = clientUpdateService.getUpdatedVersions(app, platform, version);
        return GlobalResponse.success(clientUpdates);
    }
}
