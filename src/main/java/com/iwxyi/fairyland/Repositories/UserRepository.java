package com.iwxyi.fairyland.Repositories;

import com.iwxyi.fairyland.Models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    
    User findByUserId(Long userId);

    User findByPhoneNumber(String phoneNumber);
    
    User findByUsername(String username);
    
    User findByNickname(String nickname);
    
    User findByUsernameOrPhoneNumberOrEmailAddress(String username, String phoneNumber, String emailAddress);

}
