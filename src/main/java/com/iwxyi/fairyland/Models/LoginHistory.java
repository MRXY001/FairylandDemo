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
public class LoginHistory { 
    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loginId;
    private Long userId;
    private String ip;
    private String cpuId;
    private String loginBy;
    private boolean success;
    private String message;
    private Date time;
}
