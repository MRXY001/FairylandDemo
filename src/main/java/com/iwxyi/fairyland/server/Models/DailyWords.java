package com.iwxyi.fairyland.server.Models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class DailyWords {
    @Id
    private Date date;
    private Long userId;
    private int words;
    private int times;

    public DailyWords(Date date, Long userId) {
        this.date = date;
        this.userId = userId;
    }
}
