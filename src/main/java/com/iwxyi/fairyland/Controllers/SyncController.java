package com.iwxyi.fairyland.Controllers;

import java.util.ArrayList;
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
        // 获取云端的作品
        List<SyncBook> cloudBooks = bookService.getUserBooks(userId);
        List<SyncBook> responseBooks = new ArrayList<SyncBook>();

        // #先匹配客户端有ID的情况，最优先
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
                    // ?可能书名不一样，另一设备客户端重命名，导致书名不一样
                    // *客户端需要额外判断ID的情况，有必要时进行重命名
                    responseBooks.add(cloudBook);
                    localBooks.remove(i--);
                    cloudBooks.remove(j);
                    break;
                }
            }

            if (cloudBook == null) { // 没有找到云端的，但是客户端有ID？
                // 不用管它，可能是其他设备已经删除云端，就当做它不存在啦
                // ?当然也有可能是用户自己随便改了个ID，这本书不是该用户的
                localBooks.remove(i--);
            }
        }

        // (剩下的local都是没有ID的)
        // #再匹配客户端无书、云端却有，可能是另一设备先行上传
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
                // *云端有，但是客户端没有，需要在客户端进行创建
                responseBooks.add(cloudBook);
            }
        }

        // #最后是客户端新书，云端也没有的，创建
        for (int i = 0; i < localBooks.size(); i++) {
            SyncBook localBook = localBooks.get(i);
            // *创建云端新书
            localBook.setModifyTime(0L);
            localBook.setUploadTime(0L);
            localBook.setUserId(userId);
            SyncBook cloudBook = bookService.save(localBook);
            // 返回云端创建的对象
            responseBooks.add(cloudBook);
        }

        return GlobalResponse.map("cloudBooks", responseBooks);
    }

    /**
     * #云同步第二步：获取上次拉取后的目录内容
     * 不包含章节、名片等正文片段类的小数据
     * !查询后全部返回
     * 
     * @param syncTime 设备上次拉取的时间戳。如果为0的话表示全部下载
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
     * !可能数据量会特别巨大（一本书几M），达到上百M，以至于中间容易断开
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
     */
    @PostMapping(value = "/uploadBookCatalog")
    @ResponseBody
    @LoginRequired
    public GlobalResponse<?> uploadBookCatalog(@LoginUser Long userId, @RequestParam("bookIndex") final Long bookIndex,
            @RequestParam("catalog") final String catalog, @RequestParam("modifyTime") Long modifyTime) {

        SyncBook book = bookService.getBook(bookIndex, userId);
        book.setCatalog(catalog);
        book.setModifyTime(modifyTime);
        book.setUploadTime(System.currentTimeMillis());
        bookService.save(book);

        return new GlobalResponse<>();
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
            @RequestParam("bookIndex") final Long bookIndex, @RequestParam("chaterId") final String chapterId,
            @RequestParam("title") final String title, @RequestParam("content") final String content,
            @RequestParam(value = "chapterType", required = false) final int chapterType, @RequestParam("modifyTime") long modifyTime) {

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

        bookService.deleteBook(bookIndex, userId);

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

        bookService.restoreBook(bookIndex, userId);

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
    @PostMapping(value = "/cleanRecycle")
    @ResponseBody
    @LoginRequired
    public GlobalResponse<?> cleanBook(@LoginUser Long userId, @RequestParam("bookIndex") Long bookIndex) {

        bookService.cleanBook(bookIndex, userId);

        return GlobalResponse.success();
    }
}
