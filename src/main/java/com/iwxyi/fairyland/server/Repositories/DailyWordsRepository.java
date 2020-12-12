package com.iwxyi.fairyland.server.Repositories;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.iwxyi.fairyland.server.Models.DailyWords;

import org.springframework.data.repository.CrudRepository;

public interface DailyWordsRepository extends CrudRepository<DailyWords, Long> {

    public static Date toDailyUpdateTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 3);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        date = calendar.getTime();
        return date;
    }

    DailyWords findByDateAndUserId(Long timestamp, Long userId);

    List<DailyWords> findByDate(Long timestamp);

    List<DailyWords> findByUserId(Long userId);
}
