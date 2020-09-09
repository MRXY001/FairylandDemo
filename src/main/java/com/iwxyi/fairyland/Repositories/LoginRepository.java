package com.iwxyi.fairyland.Repositories;

import java.util.List;

import com.iwxyi.fairyland.Models.LoginHistory;

import org.springframework.data.repository.CrudRepository;

public interface LoginRepository extends CrudRepository<LoginHistory, Long> {
    List<LoginHistory> findByUserId(String userId);

}
