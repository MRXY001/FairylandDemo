package com.iwxyi.fairyland.Repositories;

import java.sql.Timestamp;
import java.util.List;

import com.iwxyi.fairyland.Models.SyncChapter;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface SyncChapterRepository extends CrudRepository<SyncChapter, Long> {
    SyncChapter findByChapterIndex(@Param("chapterIndex") Long chapterIndex);

    @Query("select * from sync_chapter where user_id = :userId and deleted != 1 order by modify_time desc")
    List<SyncChapter> getUserChapters(@Param("userId") Long userId);

    @Query("select * from sync_chapter where user_id = :userId and deleted != 1 and modify_time > :earlyTime order by modify_time desc")
    List<SyncChapter> getUserUpdatedChapters(@Param("userId") Long userId, @Param("earlyTime") Timestamp earlyTime);

    @Query("select * from sync_chapter where and user_id = :userId and deleted != 1 and modify_time > :earlyTime order by modify_time desc")
    List<SyncChapter> getBookUpdatedChapters(@Param("bookIndex") Long bookIndex, @Param("userId") Long userId,
            @Param("earlyTime") Timestamp earlyTime);
}
