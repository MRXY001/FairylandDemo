package com.iwxyi.fairyland.server.Models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity // 标记持久化类，自动建表 user。若不同可用 @Table(name="")
@Data // 自动生成get set equals hashCode toString等
@NoArgsConstructor
public class AwardMedal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 表示主键由数据库生成，自增
    private Long awardId;
    private Long userId;
    private Long medalId;
    private String desc;
    private Date create_time;
    
    public AwardMedal(Long userId, Long medalId, String desc, Date createTime) {
        this.userId = userId;
        this.medalId = medalId;
        this.desc = desc;
        this.create_time = createTime;
    }
}
