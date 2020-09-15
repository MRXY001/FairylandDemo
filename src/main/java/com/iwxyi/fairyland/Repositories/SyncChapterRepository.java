package com.iwxyi.fairyland.Repositories;

import java.util.List;

import javax.transaction.Transactional;

import com.iwxyi.fairyland.Models.SyncChapter;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface SyncChapterRepository extends CrudRepository<SyncChapter, Long> {
    SyncChapter findByChapterIndex(Long chapterIndex);

    List<SyncChapter> findByUserId(Long userId);

    List<SyncChapter> findByUserIdAndDeletedNotAndBookDeletedNotModifyTimeGreaterThan(Long userId, boolean notDeleted,
            boolean bookExist, long time);

    @Transactional // 事务
    @Modifying // 增删改必须有这个注解
    @Query(value = "update sync_chapter set book_deleted = true where book_index = :bookIndex", nativeQuery = true)
    int deleteBookChapter(Long bookIndex);

    @Transactional // 事务
    @Modifying // 增删改必须有这个注解
    @Query(value = "update sync_chapter set book_deleted = false where book_index = :bookIndex", nativeQuery = true)
    int restoreBookChapter(Long bookIndex);
}
