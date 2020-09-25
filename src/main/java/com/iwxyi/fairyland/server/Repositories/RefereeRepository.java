package com.iwxyi.fairyland.server.Repositories;

import com.iwxyi.fairyland.server.Models.Referee;

import org.springframework.data.repository.CrudRepository;

public interface RefereeRepository extends CrudRepository<Referee, Long> {
    Referee findByUserId(Long userId);
    
}
