package com.iwxyi.fairyland.Exception;

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
    
    private String msg;
    private Integer code;
}
