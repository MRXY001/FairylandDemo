package com.iwxyi.fairyland.Exception;

import com.iwxyi.fairyland.Config.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false) // 取消报错用的
@NoArgsConstructor
@AllArgsConstructor
public class FormatedException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public FormatedException(String msg) {
        this.msg = msg;
        this.code = 500;
    }
    
    public FormatedException(ErrorCode code) {
        this.code = code.code;
    }
    
    public FormatedException(String msg, ErrorCode code) {
        this.code = code.code;
        this.msg = msg;
    }
    
    private String msg;
    private Integer code;
}
