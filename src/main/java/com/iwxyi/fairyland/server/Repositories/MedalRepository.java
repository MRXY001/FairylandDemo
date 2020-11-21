package com.iwxyi.fairyland.server.Repositories;

import com.iwxyi.fairyland.server.Models.Medal;

import org.springframework.data.repository.CrudRepository;

public interface MedalRepository extends CrudRepository<Medal, Long> {
    Medal findByMedalId(Long id);
    Medal findByCode(String code);
}
