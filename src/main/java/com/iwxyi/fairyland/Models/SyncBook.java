package com.iwxyi.fairyland.Models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class SyncBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookIndex;
    private Long userId;
    @Length(min = 1, max = 10, message = "书名不能超过10个字")
    @Pattern(regexp = "^([\\u4e00-\\u9fa5\\w\\d][\\u4e00-\\u9fa5\\w\\d:：.“”\"]{0,9})$", message = "书名只能由中英文、数字、冒号组成，且不能为符号开头")
    private String bookName;
    private String catalog; // 目录正文
    private int publicState; // 0不发布，1仅为好友可见，2全部公开
    private long createTime;
    private long uploadTime = 0L;
    private long modifyTime = 0L;
    private int publishState;
    private boolean deleted;

}
