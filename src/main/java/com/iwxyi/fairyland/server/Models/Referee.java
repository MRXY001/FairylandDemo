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
 * 推荐人
 */
@Entity
@Data
@NoArgsConstructor
public class Referee {
    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    Long userId; // 被推荐的用户Id
    Long refereeId; // 推荐给user的用户Id
    double amount = 0; // 总返利金额（包括已提现）
    double withdrawal = 0; // 已提现
    Date createTime; // 推荐时间
}
