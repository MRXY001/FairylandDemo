package com.iwxyi.fairyland.server.Models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;

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
    @NotBlank(message = "账号不能为空")
    @Length(min = 2, max = 255, message = "用户名长度必须在{min}~{max}之间")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5\\w\\d_]{2,16}$", message = "账号只允许2~255位的汉字/大小写字母/数字/下划线")
    private String username;
    @NotBlank(message = "密码不能为空")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // 从JSON中读取，但不保存到JSON
    private String passwordHash;
    @NotBlank(message = "昵称不能为空")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5\\w\\d_]{2,16}$", message = "昵称只允许2~16位的汉字/大小写字母/数字/下划线")
    @Length(min = 2, max = 16, message = "昵称长度必须在{min}~{max}之间")
    private String nickname;
    @Length(min = 11, max = 11, message = "请输入正确的手机号")
    @Pattern(regexp = "^(((13[0-9])|(14[579])|(15([0-3]|[5-9]))|(16[6])|(17[0135678])|(18[0-9])|(19[89]))\\d{8})$", message = "请输入正确的手机号")
    private String phoneNumber;

    @Min(0)
    @Max(100)
    private int age;
    @Min(0)
    @Max(2)
    private int sex; // 0女，1男
    @Min(0)
    @Max(3)
    private int permission; // 权限：1小管理员，2大管理员，3开发级
    @Min(0)
    @Max(3)
    private int blocking; // 封禁：1禁言，2冻结，3封号
    @Pattern(regexp = "^(\\d{5,10})?$", message = "QQ号格式错误")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String qqNumber;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String wxUnionid;
    @Pattern(regexp = "^([a-zA-Z0-9]+([-_.][a-zA-Z0-9]+)*@[a-zA-Z0-9]+([-_.][a-zA-Z0-9]+)*\\.[a-z]{2,})?$", message = "邮箱格式错误")
    private String emailAddress;
    @Length(min = 0, max = 20, message = "签名长度不能超过20字")
    private String motto;
    @Pattern(regexp = "^((http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&:/~\\+#]*[\\w\\-\\@?^=%&/~\\+#])?)?$", message = "个人主页配置出错")
    private String homePage;
    // private int roomMaxCount = 3; // 默认可以加入多少个房间
    private int roomJoinedCount; // 当前加入了多少个房间

    private int level;
    private int rank;
    private int integral;
    @Min(0)
    private int allWords;
    @Min(0)
    private int allUseds;
    @Min(0)
    private int allTimes;
    private int allBonus;
    @Min(0)
    private int wordsToday;
    @Min(0)
    private int allWordsYesterday;
    @Min(0)
    private int wordsYesterday;
    @Min(0)
    private int allTimesYesterday;
    @Min(0)
    private int codeSpeed; // 码字速度

    @Transient // 不存到数据库里
    private boolean vip; // 是否是VIP
    private Date vipDeadline; // VIP到期时间
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Min(0)
    private double totalPay; // 总共付了多少钱（实付）

    private Date activeTime = new Date(0); // 活动时间
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createTime;

    public User(String username, String passwordHash, String phoneNumber, Date createTime) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.phoneNumber = phoneNumber;
        this.nickname = username;
        this.createTime = createTime;
    }

    // 方便调试时查看数据
    public String simpleString() {
        return "User{ " + "id=" + userId + ", username=" + username + "}";
    }
}
