package com.iwxyi.fairyland.Models;

import java.sql.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity // 标记持久化类，自动建表 user。若不同可用 @Table(name="")
@Data // 自动生成get set equals hashCode toString等
@NoArgsConstructor
public class User {
    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 表示主键由数据库生成，自增
    private Long userId;
    @Size(min = 2, max = 16, message = "用户名长度必须在{min}~{max}之间")
    private String username;
    private String passwordHash;

    @Size(min = 2, max = 16, message = "昵称长度必须在{min}~{max}之间")
    private String nickname;
    @NotBlank(message = "手机号码不能为空")
    @Size(min = 11, max = 11, message = "手机号码长度不正确")
    @Pattern(regexp = "^(((13[0-9])|(14[579])|(15([0-3]|[5-9]))|(16[6])|(17[0135678])|(18[0-9])|(19[89]))\\d{8})$", message = "手机号格式错误")
    private String phoneNumber;
    private int age;
    private int sex; // 0女，1男
    private String identityCard;
    private int permission; // 权限：1小管理员，2大管理员，3开发级
    private int blocking; // 权限：1小管理员，2大管理员，3开发级
    private String qqNumber;
    private String wxUnionid;
    private String mail;
    private String mooto;
    private String homePage;

    private int level;
    private int integral;
    private int allWords;
    private int allTimes;
    private int allBonus;
    private int allWordsYesterday;
    private int wordsYesterday;
    private int writeSpeed; // 每小时多少字

    @Transient // 不存到数据库里
    private boolean vip; // 是否是VIP
    private double totalPay; // 总共付了多少钱（实付）

    private int loginFailed;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date syncTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createTime;

    // 方便调试时查看数据
    public String simpleString() {
        return "User{ " + "id=" + userId + ", username=" + username + "}";
    }
}
