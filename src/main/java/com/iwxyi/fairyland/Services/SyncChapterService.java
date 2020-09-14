package com.iwxyi.fairyland.Services;

import java.sql.Timestamp;
import java.util.List;

import com.iwxyi.fairyland.Config.ErrorCode;
import com.iwxyi.fairyland.Exception.FormatedException;
import com.iwxyi.fairyland.Models.SyncChapter;
import com.iwxyi.fairyland.Repositories.SyncChapterRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SyncChapterService {
    @Autowired
    SyncChapterRepository chapterRepository;

    public List<SyncChapter> getUserChapters(Long userId) {
        return chapterRepository.getUserChapters(userId);
    }

    public List<SyncChapter> getUserUpdatedChapters(Long userId, Timestamp timestamp) {
        return chapterRepository.getUserUpdatedChapters(userId, timestamp);
    }
    
    public SyncChapter getChapter(Long chapterIndex, Long bookIndex, Long userId) {
        SyncChapter chapter = chapterRepository.findByChapterIndex(chapterIndex);
        if (chapter != null) {
            if (chapter.getBookIndex() != bookIndex || chapter.getUserId() != userId) {
                // !虽然找到了，但不是这本书或者不是这个用户的
                throw new FormatedException("未找到章节", ErrorCode.NotExist);
            }
        }
        return chapter;
    }

    public SyncChapter save(SyncChapter chapter) {
        return chapterRepository.save(chapter);
    }
}
