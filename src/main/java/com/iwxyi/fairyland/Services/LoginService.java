package com.iwxyi.fairyland.Services;

import java.util.List;

import com.iwxyi.fairyland.Models.LoginHistory;
import com.iwxyi.fairyland.Repositories.LoginRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    @Autowired
    LoginRepository loginRepository;
    
    public void saveLogin(Long userId, String loginBy, String ip, String message)
    {
        LoginHistory history = new LoginHistory();
        history.setUserId(userId);
        history.setLoginBy(loginBy);
        history.setIp(ip);
        history.setMessage(message);
        loginRepository.save(history);
    }
    
    public List<LoginHistory> getLoginHistories(String userId)
    {
        return loginRepository.findByUserId(userId);
    }
    
}
