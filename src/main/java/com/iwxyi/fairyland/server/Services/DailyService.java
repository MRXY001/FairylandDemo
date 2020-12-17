package com.iwxyi.fairyland.server.Services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.iwxyi.fairyland.server.Exception.FormatedException;
import com.iwxyi.fairyland.server.Models.AwardMedal;
import com.iwxyi.fairyland.server.Models.DailyPersist;
import com.iwxyi.fairyland.server.Models.DailyWords;
import com.iwxyi.fairyland.server.Models.Medal;
import com.iwxyi.fairyland.server.Models.Room;
import com.iwxyi.fairyland.server.Models.User;
import com.iwxyi.fairyland.server.Models.UserAddition;
import com.iwxyi.fairyland.server.Repositories.AwardMedalRepository;
import com.iwxyi.fairyland.server.Repositories.DailyPersistRepository;
import com.iwxyi.fairyland.server.Repositories.DailyWordsRepository;
import com.iwxyi.fairyland.server.Repositories.MedalRepository;
import com.iwxyi.fairyland.server.Repositories.RoomRepository;
import com.iwxyi.fairyland.server.Repositories.UserAdditionRepository;
import com.iwxyi.fairyland.server.Repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DailyService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    RoomService roomService;
    @Autowired
    MedalRepository medalRepository;
    @Autowired
    AwardMedalRepository awardRepository;
    @Autowired
    DailyPersistRepository persistRepository;
    @Autowired
    UserAdditionRepository userAdditionRepository;
    @Autowired
    DailyWordsRepository dailyWordsRepository;
	
    /**
     * 更新每天的字数
     * wordsToday = 0
     * wordsYesterday = allWords - allWordsYesterday
     * allWordsYesterday = allWords
     * timesYesterday = allTimes
     */
    public void updateDailyWords() {
        // #获取上次更新（昨天3点）的日期
        Date date = DailyWordsRepository.toDailyUpdateTime(new Date());
        long timestamp = date.getTime(); // 必定准点的，精确到秒
        System.out.println(date.toString());

        // #更新上次之后活动过的账号
        List<User> users = userRepository.findByActiveTimeGreaterThan(date);
        for (User user : users) {
            System.out.println("更新用户：" + user.getNickname());
            // 获取每日字数等级
            int exp = user.getAllWords() + user.getAllTimes() + user.getAllUseds() / 10 + user.getAllBonus();
            int level = Double.valueOf(Math.sqrt(exp / 100 + 1)).intValue();
            int wordsYesterday = user.getAllWords() - user.getAllWordsYesterday();
            int timesYesterday = user.getAllTimes() - user.getAllTimesYesterday();
            
            // 保存到历史记录
            DailyWords dailyWords = new DailyWords(date, user.getUserId());
            dailyWords.setWords(wordsYesterday);
            dailyWords.setTimes(timesYesterday);
            dailyWordsRepository.save(dailyWords); // 已存在的话会覆盖，所以不用担心
            
            // 保存到用户记录
            user.setIntegral(exp);
            user.setLevel(level);
            user.setWordsToday(0);
            user.setWordsYesterday(wordsYesterday);
            user.setAllWordsYesterday(user.getAllWords());
            user.setAllTimesYesterday(user.getAllTimes());
            System.out.println("    字数：day=" + wordsYesterday + ", all=" + user.getAllWords());

            // 更新连续码字持续时间
            DailyPersist persist = persistRepository.findByUserId(user.getUserId());
            if (persist == null) {
                persist = new DailyPersist(user.getUserId());
            }
            if (wordsYesterday > 0) {
                persist.setDwc0(persist.getDwc0() + 1);
                if (persist.getDwc0() == 365) {
                    Medal medal = medalRepository.findByCode("dwc0");
                    if (medal == null) {
                        throw new FormatedException("请创建dwc0的勋章");
                    }
                    AwardMedal award = awardRepository.findByUserIdAndMedalId(user.getUserId(), medal.getMedalId());
                    if (award == null) {
                        award = new AwardMedal(user.getUserId(), medal.getMedalId(), "连续码字365天", new Date());
                        awardRepository.save(award);
                    }
                }
            } else {
                persist.setDwc0(0);
            }
            if (wordsYesterday > 1000) {
                persist.setDwc1(persist.getDwc1() + 1);
                if (persist.getDwc1() == 100) {
                    Medal medal = medalRepository.findByCode("dwc1");
                    if (medal == null) {
                        throw new FormatedException("请创建dwc1的勋章");
                    }
                    AwardMedal award = awardRepository.findByUserIdAndMedalId(user.getUserId(), medal.getMedalId());
                    if (award == null) {
                        award = new AwardMedal(user.getUserId(), medal.getMedalId(), "连续100天码1000字以上", new Date());
                        awardRepository.save(award);
                    }
                }
            } else {
                persist.setDwc1(0);
            }
            if (wordsYesterday > 3000) {
                persist.setDwc2(persist.getDwc2() + 1);
                if (persist.getDwc2() == 30) {
                    Medal medal = medalRepository.findByCode("dwc2");
                    if (medal == null) {
                        throw new FormatedException("请创建dwc2的勋章");
                    }
                    AwardMedal award = awardRepository.findByUserIdAndMedalId(user.getUserId(), medal.getMedalId());
                    if (award == null) {
                        award = new AwardMedal(user.getUserId(), medal.getMedalId(), "连续30天码3000字以上", new Date());
                        awardRepository.save(award);
                    }
                }
            } else {
                persist.setDwc2(0);
            }
            if (wordsYesterday > 5000) {
                persist.setDwc3(persist.getDwc3() + 1);
                if (persist.getDwc3() == 15) {
                    Medal medal = medalRepository.findByCode("dwc3");
                    if (medal == null) {
                        throw new FormatedException("请创建dwc3的勋章");
                    }
                    AwardMedal award = awardRepository.findByUserIdAndMedalId(user.getUserId(), medal.getMedalId());
                    if (award == null) {
                        award = new AwardMedal(user.getUserId(), medal.getMedalId(), "连续15天码5000字以上", new Date());
                        awardRepository.save(award);
                    }
                }
            } else {
                persist.setDwc3(0);
            }
            if (wordsYesterday > 8000) {
                persist.setDwc4(persist.getDwc4() + 1);
                if (persist.getDwc4() == 7) {
                    Medal medal = medalRepository.findByCode("dwc4");
                    if (medal == null) {
                        throw new FormatedException("请创建dwc4的勋章");
                    }
                    AwardMedal award = awardRepository.findByUserIdAndMedalId(user.getUserId(), medal.getMedalId());
                    if (award == null) {
                        award = new AwardMedal(user.getUserId(), medal.getMedalId(), "连续7天码8000字以上", new Date());
                        awardRepository.save(award);
                    }
                }
            } else {
                persist.setDwc4(0);
            }
            if (wordsYesterday > 10000) {
                persist.setDwc5(persist.getDwc5() + 1);
                if (persist.getDwc5() == 5) {
                    Medal medal = medalRepository.findByCode("dwc5");
                    if (medal == null) {
                        throw new FormatedException("请创建dwc5的勋章");
                    }
                    AwardMedal award = awardRepository.findByUserIdAndMedalId(user.getUserId(), medal.getMedalId());
                    if (award == null) {
                        award = new AwardMedal(user.getUserId(), medal.getMedalId(), "连续5天码10000字以上", new Date());
                        awardRepository.save(award);
                    }
                }
            } else {
                persist.setDwc5(0);
            }
            if (wordsYesterday > 15000) {
                persist.setDwc6(persist.getDwc6() + 1);
                if (persist.getDwc6() == 3) {
                    Medal medal = medalRepository.findByCode("dwc6");
                    if (medal == null) {
                        throw new FormatedException("请创建dwc6的勋章");
                    }
                    AwardMedal award = awardRepository.findByUserIdAndMedalId(user.getUserId(), medal.getMedalId());
                    if (award == null) {
                        award = new AwardMedal(user.getUserId(), medal.getMedalId(), "连续3天码15000字以上", new Date());
                        awardRepository.save(award);
                    }
                }
            } else {
                persist.setDwc6(0);
            }
            if (wordsYesterday > 30000) {
                persist.setDwc7(persist.getDwc7() + 1);
                if (persist.getDwc7() == 1) {
                    Medal medal = medalRepository.findByCode("dwc7");
                    if (medal == null) {
                        throw new FormatedException("请创建dwc7的勋章");
                    }
                    AwardMedal award = awardRepository.findByUserIdAndMedalId(user.getUserId(), medal.getMedalId());
                    if (award == null) {
                        award = new AwardMedal(user.getUserId(), medal.getMedalId(), "一天码30000字以上", new Date());
                        awardRepository.save(award);
                    }
                }
            } else {
                persist.setDwc7(0);
            }
            persistRepository.save(persist);

            // #修改每天可创建房间数量
            UserAddition userAddition = userAdditionRepository.findByUserId(user.getUserId());
            userAddition.setRoomHadCreateCount(0);
            userAdditionRepository.save(userAddition);
            
            // TODO:计算用户的成就值
            userRepository.save(user);
        }

        // #修改房间等级
        Iterable<Room> rooms = roomRepository.findAll();
        for (Room room : rooms) {
            int integral = room.getIntegral();
            int integralYesterday = room.getIntegralYesterday();
            int yesterdayIntegral = integral - integralYesterday;
            room.setYesterdayIntegral(yesterdayIntegral);
            
            // 非官方房间，无人码字
            if (yesterdayIntegral == 0 && !room.isOfficial()) {
                int cut = 1000 * room.getMemberCount();
                room.setIntegral(integral - cut); // 可以是负的！
            }
            
            int level = Double.valueOf(Math.sqrt(integral / 100 + 1)).intValue();
            room.setLevel(level);
            roomRepository.save(room);
            
            // 积分变为负的，则解散
            if (room.getIntegral() < 0) {
                // *解散房间
                roomService.disbandRoom(room.getOwnerId(), room.getRoomId());
            }
        }
        
    }

    /**
     * 更新每个小时的字数
     * wordsToday = allWords - allWordsYesterday
     */
    public void updateHourlyWords() {
        // #获取上次更新（昨天3点）的日期
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, -1); // 一小时以前
        calendar.add(Calendar.MINUTE, -1); // 留出提前一分钟提前量
        calendar.set(Calendar.SECOND, 0);
        date = calendar.getTime();

        // #更新上次之后活动过的账号
        List<User> users = userRepository.findByActiveTimeGreaterThan(date);
        for (User user : users) {
            user.setWordsToday(user.getAllWords() - user.getAllWordsYesterday());
            userRepository.save(user);
        }
    }
}
