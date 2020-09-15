package com.iwxyi.fairyland.Services;

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
        // return chapterRepository.getUserChapters(userId);
        return chapterRepository.findByUserId(userId);
    }

    public List<SyncChapter> getUserUpdatedChapters(Long userId, long timestamp) {
        // return chapterRepository.getUserUpdatedChapters(userId, timestamp);
        return chapterRepository.findByUserIdAndDeletedNotAndBookDeletedNotModifyTimeGreaterThan(userId, true, true, timestamp);
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
    
    public void renameChapter(Long chapterIndex, Long userId, String newName) {
        SyncChapter chapter = chapterRepository.findByChapterIndex(chapterIndex);
        if (chapter == null) {
            throw new FormatedException("章节不存在", ErrorCode.NotExist);
        } else if (chapter.getUserId() != userId) {
            // !不是这个用户的章节
            throw new FormatedException("不能操作非自己的章节", ErrorCode.User);
        }
        chapter.setTitle(newName);
        chapterRepository.save(chapter);
    }
    
    public void deleteChapter(Long chapterIndex, Long userId) {
        SyncChapter chapter = chapterRepository.findByChapterIndex(chapterIndex);
        if (chapter == null) {
            throw new FormatedException("章节不存在", ErrorCode.NotExist);
        } else if (chapter.getUserId() != userId) {
            // !不是这个用户的章节
            throw new FormatedException("不能操作非自己的章节", ErrorCode.User);
        }
        chapter.setDeleted(true);
        chapterRepository.save(chapter);
    }

    public void restoreChapter(Long chapterIndex, Long userId) {
        SyncChapter chapter = chapterRepository.findByChapterIndex(chapterIndex);
        if (chapter == null) {
            throw new FormatedException("章节不存在", ErrorCode.NotExist);
        } else if (chapter.getUserId() != userId) {
            // !不是这个用户的章节
            throw new FormatedException("不能操作非自己的章节", ErrorCode.User);
        }
        chapter.setDeleted(false);
        chapterRepository.save(chapter);
    }
}
