package com.iwxyi.fairyland.Models;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.hibernate.validator.constraints.Length;
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
    @Length(min = 2, max = 16, message = "用户名长度必须在{min}~{max}之间")
    @Pattern(regexp = "^\\w[\\w\\d_]{1,15}$", message = "用户名只允许2~16位的大小写字母/数字/下划线，且要求字母开头")
    private String username;
    private String passwordHash;

    @Length(min = 2, max = 16, message = "昵称长度必须在{min}~{max}之间")
    private String nickname;
    @NotBlank(message = "手机号码不能为空")
    @Length(min = 11, max = 11, message = "请输入正确的手机号")
    @Pattern(regexp = "^(((13[0-9])|(14[579])|(15([0-3]|[5-9]))|(16[6])|(17[0135678])|(18[0-9])|(19[89]))\\d{8})$", message = "请输入正确的手机号")
    private String phoneNumber;
    @Min(0)
    @Max(100)
    private int age;
    @Min(0)
    @Max(2)
    private int sex; // 0女，1男
    @Pattern(regexp = "^([\\u4e00-\\u9fa5]{1,20}|[a-zA-Z\\.\\s]{1,20})$", message = "姓名格式错误")
    private String readname; // 真实姓名
    @Pattern(regexp = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)", message = "身份证格式错误")
    private String identityCard; // 实名身份证
    @Min(0)
    @Max(3)
    private int permission; // 权限：1小管理员，2大管理员，3开发级
    @Min(0)
    @Max(3)
    private int blocking; // 封禁：1禁言，2冻结，3封号
    @Pattern(regexp = "^\\d{5,10}$", message = "QQ号格式错误")
    private String qqNumber;
    private String wxUnionid;
    @Pattern(regexp = "^[a-zA-Z0-9]+([-_.][a-zA-Z0-9]+)*@[a-zA-Z0-9]+([-_.][a-zA-Z0-9]+)*\\.[a-z]{2,}$", message = "邮箱格式错误")
    private String mailAddress;
    @Length(min = 0, max = 20, message = "签名长度不能超过20字")
    private String mooto;
    @Pattern(regexp = "(http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&:/~\\+#]*[\\w\\-\\@?^=%&/~\\+#])?", message = "个人主页配置出错")
    private String homePage;

    private int level;
    private int integral;
    @Min(0)
    private int allWords;
    @Min(0)
    private int allTimes;
    @Min(0)
    private int allBonus;
    @Min(0)
    private int allWordsYesterday;
    @Min(0)
    private int wordsYesterday;
    @Min(0)
    private int writeSpeed; // 每小时多少字

    @Transient // 不存到数据库里
    private boolean vip; // 是否是VIP
    @Min(0)
    private double totalPay; // 总共付了多少钱（实付）

    @Min(0)
    private int loginFailedCount; // 出错次数
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
