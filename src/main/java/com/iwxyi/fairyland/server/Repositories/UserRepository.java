package com.iwxyi.fairyland.server.Repositories;

import java.util.List;

import com.iwxyi.fairyland.server.Models.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    
    User findByUserId(Long userId);

    User findByPhoneNumber(String phoneNumber);
    
    User findByUsername(String username);
    
    User findByNickname(String nickname);
    
    User findByUsernameOrPhoneNumberOrEmailAddress(String username, String phoneNumber, String emailAddress);

    Page<User> findAll(Pageable pageable);

    List<User> findAll(Specification<User> specification, Pageable pageable);
}
