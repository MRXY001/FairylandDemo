package com.iwxyi.fairyland.Services;

import java.util.List;

import com.iwxyi.fairyland.Models.SyncBook;
import com.iwxyi.fairyland.Repositories.SyncBookRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SyncBookService {
    @Autowired
    SyncBookRepository syncBookRepository;
    
    public List<SyncBook> getUserBooks(String userId) {
        return syncBookRepository.findByUserIdOrderByModifyTimeDesc(userId);
    }
    
    public SyncBook save(SyncBook syncBook) {
        return syncBookRepository.save(syncBook);
    }
    
}
