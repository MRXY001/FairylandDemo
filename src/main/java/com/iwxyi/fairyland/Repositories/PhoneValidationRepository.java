package com.iwxyi.fairyland.Repositories;

import com.iwxyi.fairyland.Models.PhoneValidation;
import org.springframework.data.repository.CrudRepository;

public interface PhoneValidationRepository extends CrudRepository<PhoneValidation, Long> {
    
    PhoneValidation findByNumberAndCaptcha(String number, String captcha);

}
