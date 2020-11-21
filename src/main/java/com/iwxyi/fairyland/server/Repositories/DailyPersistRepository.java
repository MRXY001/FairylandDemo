package com.iwxyi.fairyland.server.Repositories;

import com.iwxyi.fairyland.server.Models.DailyPersist;

import org.springframework.data.repository.CrudRepository;

public interface DailyPersistRepository extends CrudRepository<DailyPersist, Long> {
    DailyPersist findByUserId(Long userId);
    
}
