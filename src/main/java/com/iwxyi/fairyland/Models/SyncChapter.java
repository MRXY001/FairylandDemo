package com.iwxyi.fairyland.Models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class SyncChapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chapterIndex;
    private Long bookIndex;
    private Long userId;
    private String chapterId; // 这个是作品内章节唯一ID，后台不使用这个
    @Length(min = 1, max = 20, message = "章节名不能超过20个字")
    private String title;
    private String content;
    private int chapterType; // 章节种类：0章节（默认）
    private long createTime = 0L;
    private long uploadTime = 0L;
    private long modifyTime = 0L;
    private int publishState;
    private boolean bookDeleted;
    private boolean deleted;

    public SyncChapter(Long userId, Long bookIndex, String chapterId, String title, String content) {
        this.userId = userId;
        this.bookIndex = bookIndex;
        this.chapterId = chapterId;
        this.title = title;
        this.content = content;
    }
}
