package com.iwxyi.fairyland.Models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class RoomHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomHistoryId;
    private Long roomId;
    private Long userId;
    private Date joinTime;
    private Date leaveTime;
    private int leaveIntegral; // 离开时的积分

    public RoomHistory(Long roomId, Long userId, Date joinTime) {
        this.roomId = roomId;
        this.userId = userId;
        this.joinTime = joinTime;
    }

    public RoomHistory(Long roomId, Long userId, Date leaveTime, int leaveIntegral) {
        this.roomId = roomId;
        this.userId = userId;
        this.leaveTime = leaveTime;
        this.leaveIntegral = leaveIntegral;
    }

    public void leave(Date leaveTime, int leaveIntegral) {
        this.leaveTime = leaveTime;
        this.leaveIntegral = leaveIntegral;
    }
}
