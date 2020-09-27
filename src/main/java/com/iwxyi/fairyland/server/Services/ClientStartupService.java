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
    public ClientStartup saveStartup(String cpuId, Long userId, Date startupTime) {
        ClientStartup clientStartup = new ClientStartup(cpuId, userId, startupTime);
        clientStartup = startupRepository.save(clientStartup);
        return clientStartup;
    }
    
    /**
     * 保存单次关闭的数据
     */
    public void saveClose(String cpuId, Long userId, Long startupId, Date startupTime, Date closeTime, int wordCount) {
        if (startupId == null) {
            throw new FormatedException("未找到启动ID", ErrorCode.NotExist);
        }
        ClientStartup clientStartup = startupRepository.findByStartupId(startupId);
        if (clientStartup == null) {
            throw new FormatedException("未找到启动记录", ErrorCode.NotExist);
        }
        if (clientStartup.getCpuId() != cpuId || clientStartup.getStartupTime() != startupTime) {
            throw new FormatedException("启动记录数据不匹配", ErrorCode.Data);
        }
        if (clientStartup.getUserId() != userId) {
            // !不是这个用户的
            // 可能是更换账号或者重新登录，暂时不进行处理
        }
        clientStartup.setCloseTime(closeTime);
        clientStartup.setWordCount(wordCount);
        startupRepository.save(clientStartup);
    }

    /**
     * 保存多组 Id-启动-关闭-字数 时间对
     * 这需要客户端手动单次上传所有数据
     */
    public void saveStartupAndClose(List<ClientStartup> scs) {
        for (int i = 0; i < scs.size(); i++) {
            ClientStartup sc = scs.get(i);
            if (i != scs.size() - 1 && sc.getStartupId() != null) {
                // 理应最多只有最后一项才有startupId
                // 就怕是用户手动改了数据
                continue;
                // throw new FormatedException("数据错误", ErrorCode.Data);
            }
            startupRepository.save(sc);
        }
    }
}
