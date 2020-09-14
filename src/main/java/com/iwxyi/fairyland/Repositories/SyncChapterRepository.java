package com.iwxyi.fairyland.Repositories;

import java.sql.Timestamp;
import java.util.List;

import com.iwxyi.fairyland.Models.SyncChapter;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface SyncChapterRepository extends CrudRepository<SyncChapter, Long> {
    SyncChapter findByChapterIndex(Long chapterIndex);

    /* @Query("select c from sync_chapter c where user_id = :userId and deleted != 1 order by modify_time desc")
    List<SyncChapter> getUserChapters(@Param("userId") Long userId);

    @Query("select c from sync_chapter c where user_id = :userId and deleted != 1 and modify_time > :earlyTime order by modify_time desc")
    List<SyncChapter> getUserUpdatedChapters(@Param("userId") Long userId, @Param("earlyTime") Timestamp earlyTime);

    @Query("select c from sync_chapter c where book_index = :bookIndex and user_id = :userId and deleted != 1 and modify_time > :earlyTime order by modify_time desc")
    List<SyncChapter> getBookUpdatedChapters(@Param("bookIndex") Long bookIndex, @Param("userId") Long userId,
            @Param("earlyTime") Timestamp earlyTime); */
        
    List<SyncChapter> findByUserId(Long userId);
    
    List<SyncChapter> findByUserIdAndDeletedNotAndModifyTimeGreaterThan(Long userId, int notDeleted, Timestamp time);
    
}
