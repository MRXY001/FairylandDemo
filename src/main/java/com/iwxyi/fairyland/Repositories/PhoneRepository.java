package com.iwxyi.fairyland.Repositories;

import java.util.Date;
import java.util.List;

import com.iwxyi.fairyland.Models.PhoneValidation;

import org.springframework.data.repository.CrudRepository;

public interface PhoneRepository extends CrudRepository<PhoneValidation, Long> {
    
    PhoneValidation findFirstByNumberOrderByCreateTimeDesc(String number);

    List<PhoneValidation> findByNumberAndCreateTimeGreaterThan(String number, Date time);

    List<PhoneValidation> findByIpAndVerifiedAndCreateTimeGreaterThan(String ip, boolean verified, Date time);
    
    List<PhoneValidation> findByCpuIdAndVerifiedAndCreateTimeGreaterThan(String cpuId, boolean verified, Date time);

}
