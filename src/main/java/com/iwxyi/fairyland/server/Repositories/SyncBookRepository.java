package com.iwxyi.fairyland.server.Repositories;

import java.util.List;

import com.iwxyi.fairyland.server.Models.SyncBook;

import org.springframework.data.repository.CrudRepository;

public interface SyncBookRepository extends CrudRepository<SyncBook, Long> {
    List<SyncBook> findByUserIdOrderByModifyTimeDesc(Long userId);

    SyncBook findByBookIndex(Long bookIndex);
    
    SyncBook findByBookIndexAndUserId(Long bookIndex, Long userId);

    SyncBook findFirstByBookNameAndUserIdAndDeletedFalse(String bookName, Long userId);

    List<SyncBook> findByUserIdAndDeletedFalse(Long userId);

    List<SyncBook> findByUserIdAndDeletedFalseAndModifyTimeGreaterThan(Long userId, long earlyTime);

    void deleteByUserIdAndDeletedFalse(Long userId);
    
    void deleteByBookIndex(Long bookIndex);
}
