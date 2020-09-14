package com.iwxyi.fairyland.Services;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.iwxyi.fairyland.Models.LoginHistory;
import com.iwxyi.fairyland.Repositories.LoginRepository;
import com.iwxyi.fairyland.Tools.IpUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    @Autowired
    LoginRepository loginRepository;
    @Autowired
    private HttpServletRequest request;
    
    public void saveLogin(Long userId, String loginBy, String cpuId, String message)
    {
        LoginHistory history = new LoginHistory();
        history.setUserId(userId);
        history.setLoginBy(loginBy);
        history.setIp(IpUtil.getIpAddr(request));
        history.setCpuId(cpuId);
        history.setMessage(message);
        loginRepository.save(history);
    }
    
    public List<LoginHistory> getLoginHistories(String userId)
    {
        return loginRepository.findByUserId(userId);
    }
    
}
