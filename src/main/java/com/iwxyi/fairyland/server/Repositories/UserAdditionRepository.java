package com.iwxyi.fairyland.server.Repositories;

import com.iwxyi.fairyland.server.Models.User;
import com.iwxyi.fairyland.server.Models.UserAddition;

import org.springframework.data.repository.CrudRepository;

public interface UserAdditionRepository extends CrudRepository<UserAddition, Long> {
    UserAddition findByUserId(Long userId);
}
