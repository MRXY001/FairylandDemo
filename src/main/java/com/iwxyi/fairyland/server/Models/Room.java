package com.iwxyi.fairyland.server.Models;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity // 标记持久化类，自动建表 user。若不同可用 @Table(name="")
@Data // 自动生成get set equals hashCode toString等
@NoArgsConstructor
public class Room {
    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;
    @NotNull
    private Long creatorId; // 创建者的userId，永远不变
    private Long ownerId; // 当前房主的Id，可空
    @NotBlank(message = "房间名称不能为空")
    @Length(min = 2, max = 16, message = "房间名称长度必须在{min}~{max}之间")
    private String roomName;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @Length(min = 0, max = 100, message = "介绍长度必须在{min}~{max}之间")
    private String introduction;
    private int memberCount; // 当前成员数量
    private int maxCount = 20; // 允许的最大人数
    private int integral; // 当前积分（包括扣除的，例如没人码字）
    private int reducedIntegral; // 扣除掉的积分，累积=当前+扣除
    private int level; // 房间等级
    private boolean official; // 是否是官方的房间（有特权哦）
    private Date createTime;
    private boolean deleted;

    public Room(Long userId, String roomName, String password, String desc) {
        this.creatorId = userId;
        this.ownerId = userId;
        this.roomName = roomName;
        this.password = password;
        this.introduction = desc;
    }

}
