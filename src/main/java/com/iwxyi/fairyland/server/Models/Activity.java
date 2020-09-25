package com.iwxyi.fairyland.server.Models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 平台活动
 */

@Entity
@Data
@NoArgsConstructor
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long activityId;
    @NotNull
    private String name; // 活动名字
    private String introduction; // 活动介绍
    private String releaseTime; // 发布时间（早于开始时间）
    private Date startTime; // 开始时间
    private Date finishTIme; // 结束时间
    private String homePage; // 主页链接

}
