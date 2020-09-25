package com.iwxyi.fairyland.server.Models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class ClientStartup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long startupId;
    private String cpuId; // 每个设备的标识符，未读取到则没有
    private Long userId; // 用户ID，未登录则没有
    private Date startupTime; // 应用启动时间
    private Date closeTime; // 应用关闭时间

    /**
     * 每次登录都是带着上次关闭时间+本次启动时间
     * 所以closeTime一定会有个空的
     */
    public ClientStartup(String cpuId, Long userId, Date startupTime) {
        this.cpuId = cpuId;
        this.userId = userId;
        this.startupTime = startupTime;
    }
    
    /**
     * 可能会有多次打开，而只上传一次
     * 这是会有m个 startup-close 数组对，以及单个 startup
     * 最终添加 m + 1 条记录
     */
    public ClientStartup(String cpuId, Long userId, Date startupTime, Date closeTime) {
        this.cpuId = cpuId;
        this.userId = userId;
        this.startupTime = startupTime;
        this.closeTime = closeTime;
    }
}
