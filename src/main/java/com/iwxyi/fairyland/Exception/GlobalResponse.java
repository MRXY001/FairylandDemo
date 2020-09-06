package com.iwxyi.fairyland.Exception;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class GlobalResponse<T> {
    protected boolean success = false;
    private T data;
    private Integer code;
    private String msg;
    
    public GlobalResponse(T data, boolean success) {
        this.data = data;
        this.success = success;
    }
    
    public static <T> GlobalResponse<T> success(T data) {
        return new GlobalResponse<>(data, true);
    }
    
    public static <T> GlobalResponse<T> failed(String msg, Integer code) {
        GlobalResponse<T> resp = new GlobalResponse<T>();
        resp.setCode(code);
        resp.setMsg(msg);
        resp.setSuccess(false);
        return resp;
    }
}
