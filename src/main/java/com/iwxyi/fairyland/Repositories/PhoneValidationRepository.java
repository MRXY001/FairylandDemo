package com.iwxyi.fairyland.Repositories;

import com.iwxyi.fairyland.Models.PhoneValidation;
import org.springframework.data.repository.CrudRepository;

public interface PhoneValidationRepository extends CrudRepository<PhoneValidation, Long> {
    
    PhoneValidation findFirstByNumberOrderByCreateTimeDesc(String number);

}
