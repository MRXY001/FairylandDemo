package com.iwxyi.fairyland.server.Models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class RoomMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomMemberId; // 懒得使用复合主键了……
    @NotNull
    private Long roomId;
    @NotNull
    private Long userId;
    private int contribution; // 给房间的贡献（不扣除）
    @NotNull
    private Date joinTime;
    private int status; // 地位：0普通，1管理员，2房主
    public RoomMember(Long roomId, Long userId, Date joinTime) {
        this.roomId = roomId;
        this.userId = userId;
        this.joinTime = joinTime;
    }

}
