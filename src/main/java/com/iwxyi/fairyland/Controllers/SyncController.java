package com.iwxyi.fairyland.Controllers;

import java.util.List;

import com.iwxyi.fairyland.Exception.GlobalResponse;
import com.iwxyi.fairyland.Interceptor.LoginRequired;
import com.iwxyi.fairyland.Interceptor.LoginUser;
import com.iwxyi.fairyland.Models.SyncBook;
import com.iwxyi.fairyland.Models.SyncChapter;
import com.iwxyi.fairyland.Services.SyncBookService;
import com.iwxyi.fairyland.Services.SyncChapterService;
import com.iwxyi.fairyland.Services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/sync", produces = "application/json;charset=UTF-8")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*") // 解决跨域问题
public class SyncController {
    @Autowired
    UserService userService;
    @Autowired
    SyncBookService bookService;
    @Autowired
    SyncChapterService chapterService;

    /**
     * #云同步第一步：准备进行同步，进行ID的匹配等等
     * {"books": [{"name": "书名", "bookIndex": 1234, "modifyTime:": 时间戳(13)}, {书2}]}
     * !这些进行匹配的book，不需要到太多数据，尤其是别带catalog
     * 
     * @return 云端现有或应有的作品列表
     */
    @PostMapping(value = "/startSync")
    @ResponseBody
    @LoginRequired
    public GlobalResponse<?> startSync(@LoginUser Long userId, @RequestBody List<SyncBook> localBooks) {
        List<SyncBook> responseBooks = bookService.syncLocalAndCloudBooks(userId, localBooks);
        // 返回云端的作品的情况（此时该新建的已经新建，数量是：云端>=客户端）
        return GlobalResponse.map("cloudBooks", responseBooks);
    }

    /**
     * #云同步第二步：获取上次拉取后的目录内容
     * 不包含章节、名片等正文片段类的小数据
     * !查询后全部返回，仅仅目录，数据不会特别大
     * 
     * @param syncTime 设备上次拉取的时间戳。如果为0的话表示全部下载（仅限有效作品，刚创建也是0，没必要算）
     * @return 待下载的内容
     */
    @PostMapping(value = "/downloadUpdatedBooks")
    @ResponseBody
    @LoginRequired
    public GlobalResponse<?> downloadUpdatedBooks(@LoginUser Long userId,
            @RequestParam("syncTime") final long syncTime) {
        // #返回有更新的目录列表索引
        List<SyncBook> syncBooks = bookService.getUserUpdatedBooks(userId, syncTime);
        return GlobalResponse.map("books", syncBooks);
    }

    /**
     * #云同步第三步（总）：查询上次拉取之后的全部正文内容
     * 包括：章节、大纲、名片等
     * !数据量可能会特别巨大（一本书几M），达到上百M，以至于中间容易断开
     * !如果是初次同步(syncTime==0)慎用
     * 
     * @param syncTime 设备上次拉取的时间戳。如果为0的话表示全部下载
     * @return 待下载的内容（一次性）
     */
    @PostMapping(value = "/downloadUpdatedChapters")
    @ResponseBody
    @LoginRequired
    public GlobalResponse<?> downloadUpdatedChapters(@LoginUser Long userId,
            @RequestParam("syncTime") final long syncTime) {
        // #返回有更新的章节列表
        List<SyncChapter> syncChapters = chapterService.getUserUpdatedChapters(userId, syncTime);
        return GlobalResponse.map("chapters", syncChapters);
    }

    /**
     * #云同步第三步（分）：获取每一本书的正文
     * (用于解决作品数量过多，导致返回结果太大的问题)
     * 建议初次同步时使用，逐书下载
     */
    @PostMapping(value = "/downloadBookUpdatedChapters")
    @ResponseBody
    @LoginRequired
    public GlobalResponse<?> downloadBookUpdatedChapters(@LoginUser Long userId,
            @RequestParam("bookIndex") Long bookIndex, @RequestParam("syncTime") final Long syncTime) {
        // #获取这本作品有更新的章节列表
        List<SyncChapter> syncChapters = chapterService.getBookUpdatedChapters(userId, bookIndex, syncTime);
        return GlobalResponse.map("bookIndex", bookIndex, "chapters", syncChapters);
    }

    /**
     * #云同步第四步：上传作品目录
     * (允许不存在自动新建)
     */
    @PostMapping(value = "/uploadBookCatalog")
    @ResponseBody
    @LoginRequired
    public GlobalResponse<?> uploadBookCatalog(@LoginUser Long userId, @RequestParam("bookIndex") final Long bookIndex,
            @RequestParam("name") String name, @RequestParam("catalog") final String catalog,
            @RequestParam("modifyTime") long modifyTime) {
        SyncBook book = bookService.uploadBookCatalog(userId, bookIndex, name, catalog, modifyTime);
        return GlobalResponse.map("bookIndex", book.getBookIndex());
    }

    /**
     * #云同步第五步：上传作品章节或相关数据
     * *需要先存在作品实体
     * 
     * @param chapterType 章节种类，0章节，1大纲，其他以后再加(其实除了要用的章节，其余和后端关系不大)
     */
    @PostMapping(value = "/uploadChapterContent")
    @ResponseBody
    @LoginRequired
    public GlobalResponse<?> uploadChapterContent(@LoginUser Long userId,
            @RequestParam("bookIndex") final Long bookIndex, @RequestParam("chapterId") final String chapterId,
            @RequestParam("title") final String title, @RequestParam("content") final String content,
            @RequestParam(value = "chapterType", required = false) final int chapterType,
            @RequestParam("modifyTime") long modifyTime) {
        SyncChapter chapter = chapterService.uploadChapter(userId, bookIndex, chapterId, title, content, chapterType,
                modifyTime);
        return GlobalResponse.map("chapterIndex", chapter.getChapterIndex());
    }

    /**
     * 重命名作品
     * *主键外键索引，和名字无关，所以只需要修改book表中的名字即可
     */
    @PostMapping(value = "/renameBook")
    @ResponseBody
    @LoginRequired
    public GlobalResponse<?> renameBook(@LoginUser Long userId, @RequestParam("bookIndex") Long bookIndex,
            @RequestParam("newName") String newName) {
        bookService.renameBook(userId, bookIndex, newName);
        return GlobalResponse.success();
    }

    /**
     * 重命名章节
     * *这里改的是title，而不是chapterId(book内)
     */
    @PostMapping(value = "/renameChapter")
    @ResponseBody
    @LoginRequired
    public GlobalResponse<?> renameChapter(@LoginUser Long userId, @RequestParam("bookIndex") Long bookIndex,
            @RequestParam("chapterId") String chapterId, @RequestParam("newName") String newName) {
        chapterService.renameChapter(userId, bookIndex, chapterId, newName);
        return GlobalResponse.success();
    }

    /**
     * 删除作品
     * 删除时会把所有相关的数据都删除掉，比如章节等
     */
    @PostMapping(value = "/deleteBook")
    @ResponseBody
    @LoginRequired
    public GlobalResponse<?> deleteBook(@LoginUser Long userId, @RequestParam("bookIndex") Long bookIndex) {
        bookService.deleteBook(userId, bookIndex);
        return GlobalResponse.success();
    }

    @PostMapping(value = "/deleteChapter")
    @ResponseBody
    @LoginRequired
    public GlobalResponse<?> deleteChapter(@LoginUser Long userId, @RequestParam("bookIndex") Long bookIndex,
            @RequestParam("chapterId") String chapterId) {
        chapterService.deleteChapter(userId, bookIndex, chapterId);
        return GlobalResponse.success();
    }

    @PostMapping(value = "/restoreBook")
    @ResponseBody
    @LoginRequired
    public GlobalResponse<?> restoreBook(@LoginUser Long userId, @RequestParam("bookIndex") Long bookIndex) {
        bookService.restoreBook(userId, bookIndex);
        return GlobalResponse.success();
    }

    @PostMapping(value = "/restoreChapter")
    @ResponseBody
    @LoginRequired
    public GlobalResponse<?> restoreChapter(@LoginUser Long userId, @RequestParam("bookIndex") Long bookIndex,
            @RequestParam("chapterId") String chapterId) {
        chapterService.restoreChapter(userId, bookIndex, chapterId);
        return GlobalResponse.success();
    }

    /**
     * !清空作品回收站，无法恢复
     * 不影响未删除的作品
     * 如果需要删除所有作品，则需要先删除所有现有作品，再清空回收站
     */
    @PostMapping(value = "/cleanRecycle")
    @ResponseBody
    @LoginRequired
    public GlobalResponse<?> cleanRecycle(@LoginUser Long userId) {
        bookService.cleanRecycle(userId);
        return GlobalResponse.success();
    }

    /**
     * !彻底删除某一部作品，无法恢复
     * 无论该作品状态如何，都会清空掉
     */
    @PostMapping(value = "/cleanBook")
    @ResponseBody
    @LoginRequired
    public GlobalResponse<?> cleanBook(@LoginUser Long userId, @RequestParam("bookIndex") Long bookIndex) {
        bookService.cleanBook(userId, bookIndex);
        return GlobalResponse.success();
    }
}
