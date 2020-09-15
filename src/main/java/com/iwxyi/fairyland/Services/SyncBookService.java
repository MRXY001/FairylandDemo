package com.iwxyi.fairyland.Services;

import java.util.List;

import com.iwxyi.fairyland.Config.ErrorCode;
import com.iwxyi.fairyland.Exception.FormatedException;
import com.iwxyi.fairyland.Models.SyncBook;
import com.iwxyi.fairyland.Repositories.SyncBookRepository;
import com.iwxyi.fairyland.Repositories.SyncChapterRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SyncBookService {
    @Autowired
    SyncBookRepository bookRepository;
    @Autowired
    SyncChapterRepository chapterRepository;

    public List<SyncBook> getUserBooks(Long userId) {
        // return bookRepository.getUserBooks(userId);
        return bookRepository.findByUserIdAndDeletedNot(userId, true);
    }

    public List<SyncBook> getUserUpdatedBooks(Long userId, long time) {
        // return bookRepository.getUserUpdatedBooks(userId, time);
        return bookRepository.findByUserIdAndDeletedNotAndModifyTimeGreaterThan(userId, true, time);
    }

    public SyncBook save(SyncBook syncBook) {
        return bookRepository.save(syncBook);
    }

    public SyncBook getBook(Long bookIndex, Long userId) {
        SyncBook book = bookRepository.findByBookIndex(bookIndex);
        if (book == null || book.getUserId() != userId) {
            // ?这本书不是这个用户的？可能是用户好奇手动改的ID
            // (也可能是黑客轰炸，尝试挨个拉取作品)
            throw new FormatedException("未找到作品", ErrorCode.NotExist);
        }
        return book;
    }
    
    public void renameBook(Long bookIndex, Long userId, String newName) {
        SyncBook book = bookRepository.findByBookIndex(bookIndex);
        if (book == null) {
            throw new FormatedException("作品不存在", ErrorCode.NotExist);
        } else if (book.getUserId() != userId) {
            // !不是这个用户的作品
            throw new FormatedException("无法操作非自己的作品", ErrorCode.User);
        }
        book.setBookName(newName);
        bookRepository.save(book);
    }

    public void deleteBook(Long bookIndex, Long userId) {
        SyncBook book = bookRepository.findByBookIndex(bookIndex);
        if (book == null) {
            throw new FormatedException("作品不存在", ErrorCode.NotExist);
        } else if (book.getUserId() != userId) {
            // !不是这个用户的作品
            throw new FormatedException("无法操作非自己的作品", ErrorCode.User);
        }
        book.setDeleted(true);
        bookRepository.save(book);
        
        // 删除这本书的所有章节
        chapterRepository.deleteBookChapter(bookIndex);
    }

    public void restoreBook(Long bookIndex, Long userId) {
        SyncBook book = bookRepository.findByBookIndex(bookIndex);
        if (book == null) {
            throw new FormatedException("作品不存在", ErrorCode.NotExist);
        } else if (book.getUserId() != userId) {
            // !不是这个用户的作品
            throw new FormatedException("无法操作非自己的作品", ErrorCode.User);
        }
        book.setDeleted(false);
        bookRepository.save(book);

        // 删除这本书的所有章节
        chapterRepository.restoreBookChapter(bookIndex);
    }
    
    /**
     * !清空作品回收站，所有已删除的都将无法找回
     */
    public void cleanRecycle(Long userId) {
        bookRepository.deleteByUserIdAndDeleted(userId, true);
        chapterRepository.deleteByUserIdAndDeleted(userId, true);
    }
    
    /**
     * !彻底删除某一部作品
     */
    public void cleanBook(Long bookIndex, Long userId) {
        SyncBook book = bookRepository.findByBookIndex(bookIndex);
        if (book.getUserId() != userId) {
            throw new FormatedException("无法操作非自己的作品", ErrorCode.User);
        }
        bookRepository.deleteByBookIndex(bookIndex);
        chapterRepository.deleteByBookIndex(bookIndex);
    }
}
