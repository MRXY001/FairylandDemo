package com.iwxyi.fairyland.server.Services;

import com.iwxyi.fairyland.server.Repositories.VipPaymentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VipPaymentService {
    @Autowired
    VipPaymentRepository paymentRepository;
    
    
}
