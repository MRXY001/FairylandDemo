package com.iwxyi.fairyland.Services;

import com.iwxyi.fairyland.Repositories.SyncBookRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SyncBookService {
    @Autowired
    SyncBookRepository syncBookRepository;
    
    
}
