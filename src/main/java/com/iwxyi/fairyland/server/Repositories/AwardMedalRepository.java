package com.iwxyi.fairyland.server.Repositories;

import com.iwxyi.fairyland.server.Models.AwardMedal;

import org.springframework.data.repository.CrudRepository;

public interface AwardMedalRepository extends CrudRepository<AwardMedal, Long> {
    AwardMedal findByUserIdAndMedalId(Long userId, Long medalId);
}
