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
public class Medal {
    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 表示主键由数据库生成，自增
    private Long medalId;
    private String name;
    private String desc;
    private Date createTime;
    
}
