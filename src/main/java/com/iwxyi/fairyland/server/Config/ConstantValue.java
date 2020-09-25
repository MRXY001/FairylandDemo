package com.iwxyi.fairyland.server.Config;

public class ConstantValue {
    public static final int USER_NAME_MAX_LENGTH = 16; // 用户名最长长度
    public static final int NICKNAME_MODIFY_INTERVAL = 60480000; // 修改昵称间隔7天（毫秒）
    public static final int PHONE_VALIDATION_VALID = 300000; // 手机验证码有效期5分钟（毫秒）
    public static final int ROOM_LEAVE_INTERVAL = 12 * 60000; // 加入房间12小时后才能退出

    public static final double PAYMENT_REFEREE_1 = 0.2; // 一级返利
    public static final double PAYMENT_REFEREE_2 = 0.1; // 二级返利
    public static final double PAYMENT_REFEREE_3 = 0.05; // 三级返利

}
