package com.iwxyi.fairyland.server.Services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.iwxyi.fairyland.server.Config.ErrorCode;
import com.iwxyi.fairyland.server.Exception.FormatedException;
import com.iwxyi.fairyland.server.Models.ClientStartup;
import com.iwxyi.fairyland.server.Repositories.ClientStartupRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientStartupService {
    @Autowired
    ClientStartupRepository startupRepository;
    
    /**
     * 保存单次的启动时间
     * 不包含后面的关闭时间
     * @return 来确定一个ID，下次发送包含close的时间则是这个
     */
    public Map<String, Object> saveStartup(String cpuId, Long userId, Date startupTime) {
        ClientStartup clientStartup = new ClientStartup(cpuId, userId, startupTime);
        clientStartup = startupRepository.save(clientStartup);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("startupId", clientStartup.getStartupId());
        return map;
    }
    
    /**
     * 保存单次关闭的数据
     */
    public void saveClose(String cpuId, Long userId, Long startupId, Date startupTime, Date closeTime) {
        if (startupId == null) {
            throw new FormatedException("未找到启动ID", ErrorCode.NotExist);
        }
        ClientStartup clientStartup = startupRepository.findByStartupId(startupId);
        if (clientStartup == null) {
            throw new FormatedException("未找到启动ID", ErrorCode.NotExist);
        }
        if (clientStartup.getCpuId() != cpuId || clientStartup.getStartupTime() != startupTime) {
            throw new FormatedException("启动记录数据不匹配", ErrorCode.Data);
        }
        if (clientStartup.getUserId() != userId) {
            // !不是这个用户的
            // 可能是更换账号或者重新登录，暂时不进行处理
        }
        clientStartup.setCloseTime(closeTime);
        startupRepository.save(clientStartup);
    }

    /**
     * 保存多组 启动-关闭 时间对
     */
    public void saveStartupAndClose(String cpuId, Long userId, List<Date> startupTimes, List<Date> closeTimes) {
        for (int i = 0; i < startupTimes.size(); i++) {
            ClientStartup clientStartup = new ClientStartup(cpuId, userId, startupTimes.get(i), closeTimes.get(i));
            startupRepository.save(clientStartup);
        }
    }
}
