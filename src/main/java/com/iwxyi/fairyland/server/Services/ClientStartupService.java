package com.iwxyi.fairyland.server.Services;

import java.util.Date;

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
     * 保存单次关闭的数据
     */
    public void saveStartupAndClose(String cpuId, Long userId, Date startupTime, Date closeTime, int wordCount) {
        // 进行一些简单的判断
        long delta = closeTime.getTime() - startupTime.getTime();
        if (delta < 0 || delta > 24 * 60 * 60) {
            throw new FormatedException("数据错误：" + delta, ErrorCode.Data);
        }
        ClientStartup clientStartup = new ClientStartup(cpuId, userId, startupTime, closeTime, wordCount);
        startupRepository.save(clientStartup);
    }

    /**
     * 保存多组时间对
     */
    public void saveValues(String cpuId, Long userId, String pairsStr) {
        if (pairsStr == null) {
            return;
        }
        String[] pairList = pairsStr.split(";");
        for (String pair : pairList) {
            String[] valueList = pair.split("-");
            if (valueList.length < 3) {
                // 错误的数据
                continue;
            } else {
                // 起码有两个，则取前两个
                try {
                    Long time1 = Long.parseLong(valueList[0]);
                    Long time2 = Long.parseLong(valueList[1]);
                    int words = Integer.parseInt(valueList[2]);
                    saveStartupAndClose(cpuId, userId, Date(time1), Date(time2), words);
                } catch (Exception e) {
                    continue;
                }
            }

        }
    }
}
