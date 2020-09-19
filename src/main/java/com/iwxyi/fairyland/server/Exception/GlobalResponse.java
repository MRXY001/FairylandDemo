package com.iwxyi.fairyland.server.Exception;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.iwxyi.fairyland.server.Config.ErrorCode;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // null 值不会输出出来
public class GlobalResponse<T> {
    protected boolean success = false;
    private T data;
    private Integer code;
    private String msg;

    public GlobalResponse(T data, boolean success) {
        this.data = data;
        this.success = success;
    }

    public static <T> GlobalResponse<T> success() {
        return new GlobalResponse<>(null, true);
    }

    public static <T> GlobalResponse<T> success(T data) {
        return new GlobalResponse<>(data, true);
    }

    public static <T> GlobalResponse<T> fail(String msg, Integer code) {
        GlobalResponse<T> resp = new GlobalResponse<T>();
        resp.setCode(code);
        resp.setMsg(msg);
        resp.setSuccess(false);
        return resp;
    }

    public static <T> GlobalResponse<T> fail(String msg, ErrorCode code) {
        GlobalResponse<T> resp = new GlobalResponse<T>();
        resp.setCode(code.code);
        resp.setMsg(msg);
        resp.setSuccess(false);
        return resp;
    }

    public static <T> GlobalResponse<T> fail(String msg) {
        return fail(msg, 500);
    }

    public static <T> GlobalResponse<Map<String, Object>> map(String key, Object value) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put(key, value);
        return success(m);
    }

    public static <T> GlobalResponse<Map<String, Object>> map(String key1, Object value1, String key2, Object value2) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put(key1, value1);
        m.put(key2, value2);
        return success(m);
    }

    public static <T> GlobalResponse<Map<String, Object>> map(String key1, Object value1, String key2, Object value2,
            String key3, Object value3) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put(key1, value1);
        m.put(key2, value2);
        m.put(key3, value3);
        return success(m);
    }

    public static <T> GlobalResponse<Map<String, Object>> map(String key1, Object value1, String key2, Object value2,
            String key3, Object value3, String key4, Object value4) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put(key1, value1);
        m.put(key2, value2);
        m.put(key3, value3);
        m.put(key4, value4);
        return success(m);
    }
}
