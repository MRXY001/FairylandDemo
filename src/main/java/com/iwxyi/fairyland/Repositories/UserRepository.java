package com.iwxyi.fairyland.Repositories;

import com.iwxyi.fairyland.Models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    
    User findByUserId(String userId);

    User findByPhoneNumber(String phoneNumber);
    
    User findByUsername(String username);
    
    User findByNickname(String nickname);
    
    User findByUsernameOrPhoneNumberOrMailAddress(String username, String phoneNumber, String mailAddress);

}
