package com.iwxyi.fairyland.server.Services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.iwxyi.fairyland.server.Config.ErrorCode;
import com.iwxyi.fairyland.server.Exception.FormatedException;
import com.iwxyi.fairyland.server.Models.SyncBook;
import com.iwxyi.fairyland.server.Repositories.SyncBookRepository;
import com.iwxyi.fairyland.server.Repositories.SyncChapterRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class SyncBookService {
    @Autowired
    SyncBookRepository bookRepository;
    @Autowired
    SyncChapterRepository chapterRepository;

    Logger logger = LoggerFactory.getLogger(SyncBookService.class);

    public List<SyncBook> getUserBooks(Long userId) {
        return bookRepository.findByUserIdAndDeletedFalse(userId);
    }

    public List<SyncBook> getUserUpdatedBooks(Long userId, long time) {
        return bookRepository.findByUserIdAndDeletedFalseAndModifyTimeGreaterThan(userId, new Date(time));
    }

    private SyncBook getBook(Long userId, Long bookIndex) {
        SyncBook book = bookRepository.findByBookIndex(bookIndex);
        if (book != null && book.getUserId() != userId) {
            // ?这本书不是这个用户的？可能是用户好奇手动改的ID
            // (也可能是黑客轰炸，尝试挨个拉取作品)
            throw new FormatedException("未找到该用户的作品", ErrorCode.NotExist);
        }
        return book;
    }

    /**
     * 本地数据和云端的数据进行匹配
     * *该新建的新建，该重命名的重命名，改设置ID的设置ID
     */
    public List<SyncBook> syncLocalAndCloudBooks(Long userId, List<SyncBook> localBooks) {
        // 获取云端的作品
        List<SyncBook> cloudBooks = getUserBooks(userId);
        List<SyncBook> responseBooks = new ArrayList<SyncBook>();

        // #先匹配客户端有ID的情况，最优先
        for (int i = 0; i < localBooks.size(); i++) {
            SyncBook localBook = localBooks.get(i);
            if (!localBook.hasBookIndex()) {
                continue;
            }
            SyncBook cloudBook = null;
            for (int j = 0; j < cloudBooks.size(); j++) {
                // 云端book肯定都是有ID的
                if (cloudBooks.get(j).getBookIndex() == localBook.getBookIndex()) {
                    // ID 一模一样，那就是这本书了！
                    cloudBook = cloudBooks.get(j);
                    logger.info("匹配ID：" + localBook.getBookName());
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
                logger.info("未找到本地ID：" + localBook.getBookName() + ", id:" + localBook.getBookIndex());
                localBooks.remove(i--);
            }
        }

        // (剩下的local都是没有ID的)
        // #再匹配同名作品，客户端无ID、云端却有，可能是另一设备先行上传
        for (int i = 0; i < cloudBooks.size(); i++) {
            SyncBook cloudBook = cloudBooks.get(i);
            SyncBook localBook = null;
            for (int j = 0; j < localBooks.size(); j++) {
                if (cloudBook.getBookName().equals(localBooks.get(j).getBookName())) {
                    // 名字一样，就是这本书了！
                    localBook = localBooks.get(j);
                    logger.info("设置ID：" + localBook.getBookName());
                    // *需要在客户端设置bookIndex
                    responseBooks.add(cloudBook);
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
            SyncBook cloudBook = createBook(localBook, userId);
            logger.info("创建新书：" + localBook.getBookName());
            // 返回云端创建的对象
            responseBooks.add(cloudBook);
        }

        return responseBooks;
    }

    /**
     * 根据客户端的作品，创建一份云端的数据
     * (此时数据不全，需要后续重新上传)
     */
    public SyncBook createBook(SyncBook localBook, Long userId) {
        localBook.setUploadTime(new Date());
        localBook.setUpdateTime(new Date());
        localBook.setModifyTime(null);
        localBook.setUserId(userId);
        bookRepository.save(localBook);
        return localBook;
    }

    public SyncBook uploadBookCatalog(Long userId, Long bookIndex, String name, String catalog, Date modifyTime) {
        boolean exist = true; // 是否是已经存在的作品
        SyncBook book = null;
        // (上传为0可能是客户端懒得判断……)
        if (bookIndex != null && bookIndex > 0) {
            book = getBook(userId, bookIndex); // 要是客户端把ID乱改的话，不一定找得到
        }
        if (book == null) { // 根据ID没找到
            exist = false;
            // 根据名字找
            book = bookRepository.findFirstByBookNameAndUserIdAndDeletedFalse(name, userId);
            if (book == null) {
                // *是新作品，创建
                book = new SyncBook();
                book.setUserId(userId);
                book.setBookName(name);
            }
        }
        book.setCatalog(catalog);
        book.setModifyTime(modifyTime);
        book.setUploadTime(new Date());
        book.refreshUpdateTime(modifyTime);
        bookRepository.save(book);
        return exist ? null : book;
    }

    public void renameBook(Long userId, Long bookIndex, String newName) {
        SyncBook book = bookRepository.findByBookIndex(bookIndex);
        if (book == null) {
            throw new FormatedException("作品不存在", ErrorCode.NotExist);
        } else if (book.getUserId() != userId) {
            // !不是这个用户的作品
            throw new FormatedException("无法操作非自己的作品", ErrorCode.User);
        }
        book.setBookName(newName);
        book.refreshUpdateTime(new Date());
        bookRepository.save(book);
    }

    public void deleteBook(Long userId, Long bookIndex) {
        SyncBook book = bookRepository.findByBookIndex(bookIndex);
        if (book == null) {
            throw new FormatedException("作品不存在", ErrorCode.NotExist);
        } else if (book.getUserId() != userId) {
            // !不是这个用户的作品
            throw new FormatedException("无法操作非自己的作品", ErrorCode.User);
        }
        book.setDeleted(true);
        book.refreshUpdateTime(new Date());
        bookRepository.save(book);

        // 删除这本书的所有章节
        chapterRepository.deleteBookChapter(bookIndex);
    }

    public void restoreBook(Long userId, Long bookIndex) {
        SyncBook book = bookRepository.findByBookIndex(bookIndex);
        if (book == null) {
            throw new FormatedException("作品不存在", ErrorCode.NotExist);
        } else if (book.getUserId() != userId) {
            // !不是这个用户的作品
            throw new FormatedException("无法操作非自己的作品", ErrorCode.User);
        }
        book.setDeleted(false);
        book.refreshUpdateTime(new Date());
        bookRepository.save(book);

        // 删除这本书的所有章节
        chapterRepository.restoreBookChapter(bookIndex);
    }

    /**
     * !清空作品回收站，所有已删除的都将无法找回
     */
    public void cleanRecycle(Long userId) {
        System.out.println("删除删除" + userId);
        // bookRepository.cleanUserBooks(userId);
        bookRepository.deleteByUserIdAndDeletedFalse(userId);
        chapterRepository.deleteByUserIdAndDeletedTrue(userId);
        chapterRepository.deleteByUserIdAndBookDeletedTrue(userId);
    }

    /**
     * !彻底删除某一部作品
     */
    public void cleanBook(Long userId, Long bookIndex) {
        SyncBook book = bookRepository.findByBookIndex(bookIndex);
        if (book.getUserId() != userId) {
            throw new FormatedException("无法操作非自己的作品", ErrorCode.User);
        }
        bookRepository.deleteByBookIndex(bookIndex);
        chapterRepository.deleteByBookIndex(bookIndex);
    }
}
