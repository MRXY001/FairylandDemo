package com.iwxyi.fairyland.server.Repositories;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import com.iwxyi.fairyland.server.Models.SyncChapter;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface SyncChapterRepository extends CrudRepository<SyncChapter, Long> {
    SyncChapter findByChapterIndex(Long chapterIndex);

    List<SyncChapter> findByUserId(Long userId);

    SyncChapter findFirstByUserIdAndBookIndexAndChapterId(Long userId, Long bookIndex, String chapterId);

    List<SyncChapter> findByUserIdAndDeletedAndBookDeletedAndModifyTimeGreaterThan(Long userId, boolean notDeleted,
            boolean bookExist, long time);

    List<SyncChapter> findByUserIdAndBookIndexAndDeletedAndBookDeletedAndModifyTimeGreaterThan(Long userId,
            Long bookIndex, boolean notDeleted, boolean bookExist, Date time);

    @Transactional // 事务
    @Modifying // 增删改必须有这个注解
    @Query(value = "update sync_chapter set book_deleted = true where book_index = :bookIndex", nativeQuery = true)
    int deleteBookChapter(Long bookIndex);

    @Transactional // 事务
    @Modifying // 增删改必须有这个注解
    @Query(value = "update sync_chapter set book_deleted = false where book_index = :bookIndex", nativeQuery = true)
    int restoreBookChapter(Long bookIndex);

    void deleteByUserIdAndDeletedTrue(Long userId);

    void deleteByUserIdAndBookDeletedTrue(Long userId);

    void deleteByBookIndex(Long bookIndex);
}
