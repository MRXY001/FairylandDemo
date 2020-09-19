package com.iwxyi.fairyland.server.Repositories;

import java.util.List;

import com.iwxyi.fairyland.server.Models.LoginHistory;

import org.springframework.data.repository.CrudRepository;

public interface LoginRepository extends CrudRepository<LoginHistory, Long> {
    List<LoginHistory> findByUserId(String userId);

}
