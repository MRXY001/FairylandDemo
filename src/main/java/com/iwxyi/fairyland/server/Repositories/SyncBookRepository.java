package com.iwxyi.fairyland.server.Repositories;

import java.util.List;

import com.iwxyi.fairyland.server.Models.SyncBook;

import org.springframework.data.repository.CrudRepository;

public interface SyncBookRepository extends CrudRepository<SyncBook, Long> {
    List<SyncBook> findByUserIdOrderByModifyTimeDesc(Long userId);

    SyncBook findByBookIndex(Long bookIndex);
    
    SyncBook findByBookIndexAndUserId(Long bookIndex, Long userId);

    List<SyncBook> findByUserIdAndDeleted(Long userId, boolean notDeleted);

    List<SyncBook> findByUserIdAndDeletedAndModifyTimeGreaterThan(Long userId, boolean notDeleted, long earlyTime);

    void deleteByUserIdAndDeleted(Long userId, boolean deleted);
    
    void deleteByBookIndex(Long bookIndex);
}
