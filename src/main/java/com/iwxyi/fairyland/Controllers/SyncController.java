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

        // 先匹配客户端有ID的情况，最优先
        for (int i = 0; i < localBooks.size(); i++) {
            SyncBook localBook = localBooks.get(i);
            if (localBook.getBookIndex() == null) {
                continue;
            }
            SyncBook cloudBook = null;
            for (int j = 0; j < cloudBooks.size(); j++) {
                // 云端book肯定都是有ID的
                if (cloudBooks.get(j).getBookIndex() == localBook.getBookIndex()) {
                    // ID 一模一样，那就是这本书了！
                    cloudBook = cloudBooks.get(j);
                    // 可能书名不一样，另一设备客户端重命名，导致书名不一样
                    responseBooks.add(cloudBook);
                    localBooks.remove(i--);
                    cloudBooks.remove(j);
                    break;
                }
            }

            if (cloudBook == null) { // 没有找到云端的，但是客户端有ID？
                // 不用管它，可能是其他设备已经删除云端，就当做它不存在啦
                // 当然也有可能是用户自己随便改了个ID，这本书不是该用户的
                localBooks.remove(i--);
            }
        }

        // 剩下的local都是没有ID的
        // 再匹配客户端无ID、云端却有ID，可能是另一设备先行上传
        for (int i = 0; i < cloudBooks.size(); i++) {
            SyncBook cloudBook = cloudBooks.get(i);
            SyncBook localBook = null;
            for (int j = 0; j < localBooks.size(); j++) {
                if (cloudBook.getBookName() == localBooks.get(j).getBookName()) {
                    // 名字一样，就是这本书了！
                    localBook = localBooks.get(j);
                    cloudBooks.remove(i--);
                    localBooks.remove(j--);
                    break;
                }
            }
            
            if (localBook == null) {
                // 云端有，但是客户端没有，需要在客户端进行创建
                responseBooks.add(cloudBook);
            }
        }

        // 最后是客户端新书，云端也没有的，创建
        for (int i = 0; i < localBooks.size(); i++) {
            SyncBook localBook = localBooks.get(i);
            // 创建新书
            SyncBook cloudBook = syncBookService.save(localBook);
            // 返回云端创建的对象
            responseBooks.add(cloudBook);
        }

        return GlobalResponse.map("cloudBooks", responseBooks);
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
