package com.iwxyi.fairyland.Repositories;

import java.sql.Timestamp;
import java.util.List;

import com.iwxyi.fairyland.Models.SyncBook;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface SyncBookRepository extends CrudRepository<SyncBook, Long> {
    List<SyncBook> findByUserIdOrderByModifyTimeDesc(Long userId);

    SyncBook findByBookIndex(Long bookIndex);

    // @Query("select b from sync_book b where user_id = :userId and deleted != 1 order by modify_time desc")
    // List<SyncBook> getUserBooks(@Param("userId") Long userId);
    List<SyncBook> findByUserIdAndDeletedNot(Long userId, int notDeleted);

    // @Query("select b from sync_book b where user_id = :userId and deleted != 1 and modify_time > :earlyTime order by modify_time desc")
    // List<SyncBook> getUserUpdatedBooks(@Param("userId") Long userId, @Param("earlyTime") Timestamp earlyTime);
    List<SyncBook> findByUserIdAndDeletedNotAndModifyTimeGreaterThan(Long userId, int notDeleted, Timestamp earlyTime);
}
