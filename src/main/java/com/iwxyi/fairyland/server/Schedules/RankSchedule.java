package com.iwxyi.fairyland.server.Schedules;

import com.iwxyi.fairyland.server.Services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class RankSchedule {
	@Autowired
    private UserService userService;
	/**
     * 更新每日字数：每天3点
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void updateDailyWords() {
        System.out.println("updateDailyWords");
        userService.updateDailyWords();
    }
    
    /**
     * 更新每小时的字数：每整个小时
     */
    @Scheduled(cron = "0 0 * * * *")
    public void updateHourlyWords() {
        System.out.println("updateHourlyWords");
        userService.updateHourlyWords();
    }
}
