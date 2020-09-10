package com.iwxyi.fairyland.Controllers;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;

import com.iwxyi.fairyland.Config.ConstantKey;
import com.iwxyi.fairyland.Exception.GlobalResponse;
import com.iwxyi.fairyland.Models.User;
import com.iwxyi.fairyland.Services.SyncBookService;
import com.iwxyi.fairyland.Services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/sync", produces = "application/json;charset=UTF-8")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*") // 解决跨域问题
public class SyncController {
    @Autowired
    SyncBookService syncBookService;
    @Autowired
    UserService userService;
    @Autowired
    private HttpServletRequest request; // 注入到类或者方法参数中，自动获取
    // Long userId = Long.parseLong(request.getAttribute(ConstantKey.CURRENT_USER).toString());
    // User currentUser = userService.getUserByUserId(userId);

    /**
     * 获取这一段时间之后的内容
     * 包括：作品（目录）、章节、大纲、名片库、名片等
     * 
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
     * 上传作品章节或相关数据
     * 
     * @param chapterType 章节种类，0章节，1大纲
     */
    @PostMapping(value = "/uploadChapterContent")
    @ResponseBody
    public GlobalResponse<?> uploadChapterContent(String chapterId, String content, int chapterType) {
        return new GlobalResponse<>();
    }
    
    /**
     * 下载作品的目录
     * !注意所有下载都需要判断时间戳，如果本地有更新则不覆盖
     */
    @PostMapping(value = "/downloadBookCatalog")
    @ResponseBody
    public GlobalResponse<?>downloadBookCatalog(String bookId) {
        return new GlobalResponse<>();
    }
    
    /**
     * 下载章节/大纲等内容
     */
    @PostMapping(value = "/downloadChapterContent")
    @ResponseBody
    public GlobalResponse<?>downloadChapterContent(String chapterId, int chapterType) {
        return new GlobalResponse<>();
    }
}
