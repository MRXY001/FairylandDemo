package com.iwxyi.fairyland.Services;

import java.sql.Timestamp;
import java.util.List;

import com.iwxyi.fairyland.Config.ErrorCode;
import com.iwxyi.fairyland.Exception.FormatedException;
import com.iwxyi.fairyland.Models.SyncBook;
import com.iwxyi.fairyland.Repositories.SyncBookRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SyncBookService {
    @Autowired
    SyncBookRepository bookRepository;
    
    public List<SyncBook> getUserBooks(Long userId) {
        return bookRepository.getUserBooks(userId);
    }
    
    public List<SyncBook> getUserUpdatedBooks(Long userId, Timestamp time) {
        return bookRepository.getUserUpdatedBooks(userId, time);
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
    
}
