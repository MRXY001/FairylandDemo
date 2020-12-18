package com.iwxyi.fairyland.server.Models;

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
public class ClientUpdate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long versionIndex;
    private String app; // 程序(全小写)：writerfly/mzfy/fairyland
    private String platform; // 平台(全小写)：windows/mac/linux/android/ios
    private String period; // 通道：dev/pre-alpha/alpha/beta/stable
    private String channel; // 渠道：允许为空
    private Integer version; // 1010
    private String code; // 1.1.0 不带v
    private String title;
    private String desc;
    private String url;
    private Date createTime;
}
