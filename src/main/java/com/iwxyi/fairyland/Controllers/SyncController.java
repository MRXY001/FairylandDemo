package com.iwxyi.fairyland.Controllers;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;

import com.iwxyi.fairyland.Exception.GlobalResponse;
import com.iwxyi.fairyland.Services.SyncBookService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@RequestMapping(value = "/sync", produces = "application/json;charset=UTF-8")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*") // 解决跨域问题
public class SyncController {
    @Autowired
    SyncBookService syncBookService;
    
    ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
            .getRequestAttributes();
    HttpServletRequest request = servletRequestAttributes.getRequest();
    Long userId = Long.parseLong(request.getAttribute("user_id").toString());
    
    /**
     * 获取这一段时间之后的内容
     * @param time 最早的时间戳，也是设备上次同步的时间戳。如果为0的话表示全部下载
     * @return 待下载的内容
     */
    @PostMapping(value = "/downloadUpdated")
    @ResponseBody
    public GlobalResponse<?> downloadUpdated(Timestamp time) {
        
        
        
        return new GlobalResponse<>();
    }
    
    /**
     * 上传作品目录
     */
    @PostMapping(value = "/uploadBookCatalog")
    @ResponseBody
    public GlobalResponse<?> uploadBookCatalog(String bookId, String catalog) {
        return new GlobalResponse<>();
    }
    
    /**
     * 上传作品章节
     */
    @PostMapping(value = "/uploadChapterContent")
    @ResponseBody
    public GlobalResponse<?> uploadChapterContent(String chapterId, String content) {
        return new GlobalResponse<>();
    }
}
