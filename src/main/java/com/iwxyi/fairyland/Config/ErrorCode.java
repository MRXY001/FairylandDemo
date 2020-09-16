package com.iwxyi.fairyland.Config;

public enum ErrorCode {
    Other(1000), // 未知
    // 数据错误
    Arg(1001), // 参数错误
    Login(1002), // 未登录
    User(1003), // 用户错误
    Data(1004), // 数据错误
    Exist(1005), // 已存在
    NotExist(1006), // 不存在
    Blocked(1007), // 冻结
    Permission(1008), // 权限错误
    Insufficient(1009), // 数量不够
    // 验证错误
    Invalid(1010), // 无效的：未发送验证码
    Incorrect(1011), // 不正确的
    Overdue(1012), // 过期的
    Retry(1013), // 重试
    // 操作程度
    Frequency(1020), // 过于频繁（总）
    Wait(1021), // 长期操作请等待
    FrequencyTime(1022), // 时间过于频繁（一般）
    FrequencyNumber(1023), // 同一号码过于频繁
    FrequencyIp(1024), // 同一个IP操作过于频繁
    FrequencyDevice(1025), // 同一个设备操作过于频繁
    Bomb(250), // 疑似轰炸
    Test(1999); // 测试

    public final int code;

    ErrorCode(final int code) {
        this.code = code;
    }
}
