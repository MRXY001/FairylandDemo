package com.iwxyi.fairyland.Controllers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.iwxyi.fairyland.Exception.GlobalResponse;
import com.iwxyi.fairyland.Interceptor.LoginRequired;
import com.iwxyi.fairyland.Interceptor.LoginUser;
import com.iwxyi.fairyland.Models.SyncBook;
import com.iwxyi.fairyland.Services.SyncBookService;
import com.iwxyi.fairyland.Services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    /**
     * 准备进行同步，进行ID的匹配等等
     * {"books": [{"name": "书名", "bookIndex": 1234, "modifyTime:": 时间戳(13)}, {书2}]}
     * @return 云端现有或应有的作品列表
     */
    @PostMapping(value = "/startSync")
    @ResponseBody
    @LoginRequired
    public GlobalResponse<?> startSync(@LoginUser String userId, @RequestBody List<SyncBook> localBooks) {

        // 获取云端的作品
        List<SyncBook> cloudBooks = syncBookService.getUserBooks(userId);
        List<SyncBook> responseBooks = new ArrayList<SyncBook>();

        /*// 先匹配两者都有ID的情况，最优先
        
        // 再匹配本地无ID、云端有ID，可能是另一设备
        
        // 最后是本地新书，云端也没有的*/

        // 本地和云端的一一进行匹配，排除各种稀奇百怪的状况
        for (int i = 0; i < localBooks.size(); i++) {
            SyncBook localBook = localBooks.get(i);
            if (localBook.getBookIndex() == null) { // 没有ID，那么肯定还未开始同步
                // 寻找云端是否已经有作品
                SyncBook cloudBook = null;
                for (int j = 0; j < cloudBooks.size(); j++) {
                    // 判断书名是否一样
                    if (cloudBooks.get(j).getBookName().equals(localBook.getBookName())) {
                        cloudBook = cloudBooks.get(j);
                        break;
                    }
                }
                
                if (cloudBook == null) { // 云端没有，需要手动上传
                    // 创建作品
                    localBook = syncBookService.save(localBook);
                } else { // 云端已经有了，那么就是这一本书
                    localBook.setBookIndex(cloudBook.getBookIndex());
                }
                responseBooks.add(localBook); // 添加到返回的对象

            } else { // 已经同步过了（或者被用户手动破解过？）
                boolean find = false; // 云端是否已找到
                for (int j = 0; j < cloudBooks.size(); j++) {
                    SyncBook cloudBook = cloudBooks.get(j);
                    // 本地与云端匹配
                    if (cloudBook.getBookIndex() == localBook.getBookIndex()) { // 就是这一本书
                        find = true;
                        
                        // 判断这一系列的ID吧
                        
                    }
                }
                
                if (find) { // 已经匹配对应的ID，也已经处理过了，后续就不用管了
                    break;
                }
                
                // 本地有ID，但是云端没有，那么就有些奇怪了~
            }
        }

        return new GlobalResponse<>();
    }

    /**
     * 获取这一段时间之后的内容
     * 包括：作品（目录）、章节、大纲、名片库、名片等
     * @param time 最早的时间戳，也是设备上次同步的时间戳。如果为0的话表示全部下载
     * @return 待下载的内容
     */
    @PostMapping(value = "/downloadUpdated")
    @ResponseBody
    @LoginRequired
    public GlobalResponse<?> downloadUpdated(@LoginUser String userId, final Timestamp time) {

        // 返回有更新的目录列表索引

        // 返回有更新的章节列表

        return new GlobalResponse<>();
    }

    /**
     * 下载作品的目录
     * !注意所有下载都需要判断时间戳，如果本地有更新则不覆盖
     */
    @PostMapping(value = "/downloadBookCatalog")
    @ResponseBody
    @LoginRequired
    public GlobalResponse<?> downloadBookCatalog(@LoginUser String userId, final String bookId) {
        return new GlobalResponse<>();
    }

    /**
     * 下载章节/大纲等内容
     */
    @PostMapping(value = "/downloadChapterContent")
    @ResponseBody
    @LoginRequired
    public GlobalResponse<?> downloadChapterContent(@LoginUser String userId, final String chapterId,
            final int chapterType) {
        return new GlobalResponse<>();
    }

    /**
     * 上传作品目录
     */
    @PostMapping(value = "/uploadBookCatalog")
    @ResponseBody
    @LoginRequired
    public GlobalResponse<?> uploadBookCatalog(@LoginUser String userId, final String bookId, final String catalog) {
        return new GlobalResponse<>();
    }

    /**
     * 上传作品章节或相关数据
     * @param chapterType 章节种类，0章节，1大纲
     */
    @PostMapping(value = "/uploadChapterContent")
    @ResponseBody
    @LoginRequired
    public GlobalResponse<?> uploadChapterContent(@LoginUser String userId, final String chapterId,
            final String content, final int chapterType) {
        return new GlobalResponse<>();
    }
}
