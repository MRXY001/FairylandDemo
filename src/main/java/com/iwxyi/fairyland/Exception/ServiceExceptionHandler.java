package com.iwxyi.fairyland.Exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

// @ControllerAdvice
public class ServiceExceptionHandler {

    /**
     * @ExceptionHandler相当于controller的@RequestMapping 如果抛出的的是ServiceException，则调用该方法
     * @param se 业务异常
     * @return
     */
    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public Response handle(ServiceException se) {
        return ResponseUtil.error(se.getCode(), se.getMessage());
    }
}