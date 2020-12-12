package com.iwxyi.fairyland.server.Models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity // 标记持久化类，自动建表 user。若不同可用 @Table(name="")
@Data // 自动生成get set equals hashCode toString等
@NoArgsConstructor
public class UserAddition {
    @Id // 主键
    private Long userId;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Pattern(regexp = "^([\\u4e00-\\u9fa5]{1,20}|[a-zA-Z\\.\\s]{1,20})?$", message = "姓名格式错误")
    private String realname; // 真实姓名
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Pattern(regexp = "(^$)|(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)", message = "身份证格式错误")
    private String identityCard; // 实名身份证
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Min(0)
    private int loginFailedCount; // 登录出错次数
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Date loginForbidTime; // 登录多次出错，该时间前禁止登录
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Date nicknameModifyTime; // 上次修改名字，一段时间内不允许再次修改
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Date blockTime; // 被冻结时间
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Date blockDeadline; // 被冻结到什么时候
    
    private int roomMaxJoinCount;
    private int roomMaxCreateCount; // 每日最多可以创建多少房间
    private int roomHadCreateCount; // 今日已经创建了多少房间

    // 使用系统
    private boolean systemWindows;
    private boolean systemAndroid;
    private boolean systemMac;
    private boolean systemIos;
    
    public UserAddition(Long userId) {
        this.userId = userId;
    }
}
