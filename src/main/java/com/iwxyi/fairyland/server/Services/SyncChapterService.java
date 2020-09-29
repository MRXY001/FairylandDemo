package com.iwxyi.fairyland.server.Services;

import java.util.Date;
import java.util.List;

import com.iwxyi.fairyland.server.Config.ErrorCode;
import com.iwxyi.fairyland.server.Exception.FormatedException;
import com.iwxyi.fairyland.server.Models.SyncChapter;
import com.iwxyi.fairyland.server.Repositories.SyncChapterRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class SyncChapterService {
    @Autowired
    SyncChapterRepository chapterRepository;

    public List<SyncChapter> getUserChapters(Long userId) {
        return chapterRepository.findByUserId(userId);
    }

    public List<SyncChapter> getUserUpdatedChapters(Long userId, long timestamp) {
        return chapterRepository.findByUserIdAndDeletedAndBookDeletedAndModifyTimeGreaterThan(userId, false, false,
                timestamp);
    }

    public List<SyncChapter> getBookUpdatedChapters(Long userId, Long bookIndex, long timestamp) {
        return chapterRepository.findByUserIdAndBookIndexAndDeletedAndBookDeletedAndModifyTimeGreaterThan(userId,
                bookIndex, false, false, timestamp);
    }

    public SyncChapter getChapterByChapterIndex(Long chapterIndex, Long bookIndex, Long userId) {
        SyncChapter chapter = chapterRepository.findByChapterIndex(chapterIndex);
        if (chapter != null) {
            if (chapter.getBookIndex() != bookIndex || chapter.getUserId() != userId) {
                // !虽然找到了，但不是这本书或者不是这个用户的
                throw new FormatedException("未找到章节", ErrorCode.NotExist);
            }
        }
        return chapter;
    }

    public SyncChapter getChapterByChapterId(Long bookIndex, String chapterId, Long userId) {
        SyncChapter chapter = chapterRepository.findFirstByUserIdAndBookIndexAndChapterId(userId, bookIndex, chapterId);
        return chapter;
    }

    public SyncChapter save(SyncChapter chapter) {
        return chapterRepository.save(chapter);
    }

    public SyncChapter uploadChapter(Long userId, Long bookIndex, String chapterId, String title, String content,
            int chapterType, Date modifyTime) {
        SyncChapter chapter = getChapterByChapterId(bookIndex, chapterId, userId);
        if (chapter == null) {
            // *创建chapter
            chapter = new SyncChapter(userId, bookIndex, chapterId, title, content);
            chapter.setChapterType(chapterType);
            chapter.setCreateTime(new Date());
        }
        if (title != null && title.length() > 0) {
            chapter.setTitle(title);
        }
        chapter.setContent(content);
        chapter.setModifyTime(modifyTime);
        chapter.setUploadTime(new Date());
        return save(chapter);
    }

    public void renameChapter(Long userId, Long bookIndex, String chapterId, String newName) {
        SyncChapter chapter = chapterRepository.findFirstByUserIdAndBookIndexAndChapterId(userId, bookIndex, chapterId);
        if (chapter == null) {
            throw new FormatedException("章节不存在", ErrorCode.NotExist);
        } else if (chapter.getUserId() != userId) {
            // !不是这个用户的章节
            throw new FormatedException("不能操作非自己的章节", ErrorCode.User);
        }
        chapter.setTitle(newName);
        chapterRepository.save(chapter);
    }

    public void deleteChapter(Long userId, Long bookIndex, String chapterId) {
        SyncChapter chapter = chapterRepository.findFirstByUserIdAndBookIndexAndChapterId(userId, bookIndex, chapterId);
        if (chapter == null) {
            throw new FormatedException("章节不存在", ErrorCode.NotExist);
        } else if (chapter.getUserId() != userId) {
            // !不是这个用户的章节
            throw new FormatedException("不能操作非自己的章节", ErrorCode.User);
        }
        chapter.setDeleted(true);
        chapterRepository.save(chapter);
    }

    public void restoreChapter(Long userId, Long bookIndex, String chapterId) {
        SyncChapter chapter = chapterRepository.findFirstByUserIdAndBookIndexAndChapterId(userId, bookIndex, chapterId);
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
