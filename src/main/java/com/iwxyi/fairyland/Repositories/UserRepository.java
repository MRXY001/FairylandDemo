package com.iwxyi.fairyland.Repositories;

import com.iwxyi.fairyland.Models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
