package com.iwxyi.fairyland.Repositories;

import java.util.List;

import com.iwxyi.fairyland.Models.SyncBook;

import org.springframework.data.repository.CrudRepository;

public interface SyncBookRepository extends CrudRepository<SyncBook, Long> {
    List<SyncBook> findByUserIdOrderByModifyTimeDesc(Long userId);

    SyncBook findByBookIndex(Long bookIndex);
    
    SyncBook findByBookIndexAndUserId(Long bookIndex, Long userId);

    List<SyncBook> findByUserIdAndDeletedNot(Long userId, boolean notDeleted);

    List<SyncBook> findByUserIdAndDeletedNotAndModifyTimeGreaterThan(Long userId, boolean notDeleted, long earlyTime);

    void deleteByUserIdAndDeleted(Long userId, boolean deleted);
    
    void deleteByBookIndex(Long bookIndex);
}
