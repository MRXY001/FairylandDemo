package com.iwxyi.fairyland.Repositories;

import java.util.List;

import com.iwxyi.fairyland.Models.SyncBook;

import org.springframework.data.repository.CrudRepository;

public interface SyncBookRepository extends CrudRepository<SyncBook, Long> {
    List<SyncBook> findByUserIdOrderByModifyTimeDesc(Long userId);

    SyncBook findByBookIndex(Long bookIndex);

    // @Query("select b from sync_book b where user_id = :userId and deleted != 1 order by modify_time desc")
    // List<SyncBook> getUserBooks(@Param("userId") Long userId);
    List<SyncBook> findByUserIdAndDeletedNot(Long userId, boolean notDeleted);

    // @Query("select b from sync_book b where user_id = :userId and deleted != 1 and modify_time > :earlyTime order by modify_time desc")
    // List<SyncBook> getUserUpdatedBooks(@Param("userId") Long userId, @Param("earlyTime") Timestamp earlyTime);
    List<SyncBook> findByUserIdAndDeletedNotAndModifyTimeGreaterThan(Long userId, boolean notDeleted, long earlyTime);
}
