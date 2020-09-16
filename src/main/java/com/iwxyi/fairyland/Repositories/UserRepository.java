package com.iwxyi.fairyland.Repositories;

import javax.transaction.Transactional;

import com.iwxyi.fairyland.Models.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends CrudRepository<User, Long> {
    
    User findByUserId(Long userId);

    User findByPhoneNumber(String phoneNumber);
    
    User findByUsername(String username);
    
    User findByNickname(String nickname);
    
    User findByUsernameOrPhoneNumberOrEmailAddress(String username, String phoneNumber, String emailAddress);

    Page<User> findAll(Pageable pageable);
    
    @Modifying
    @Transactional
    @Query(value = "update user u set u.roomId = null where u.roomId = :roomId")
    int removeUserRoom(@Param("roomId") Long roomId);

}
