package com.iwxyi.fairyland.server.Repositories;

import java.util.Date;
import java.util.List;

import com.iwxyi.fairyland.server.Models.SyncBook;

import org.springframework.data.repository.CrudRepository;

public interface SyncBookRepository extends CrudRepository<SyncBook, Long> {
    List<SyncBook> findByUserIdOrderByModifyTimeDesc(Long userId);

    SyncBook findByBookIndex(Long bookIndex);
    
    SyncBook findByBookIndexAndUserId(Long bookIndex, Long userId);

    SyncBook findFirstByBookNameAndUserIdAndDeletedFalse(String bookName, Long userId);

    List<SyncBook> findByUserIdAndDeletedFalse(Long userId);

    List<SyncBook> findByUserIdAndDeletedFalseAndUpdateTimeGreaterThan(Long userId, Date earlyTime);

    void deleteByUserIdAndDeletedFalse(Long userId);
    
    void deleteByBookIndex(Long bookIndex);
}
