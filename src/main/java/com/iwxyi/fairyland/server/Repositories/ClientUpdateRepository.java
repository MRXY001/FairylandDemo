package com.iwxyi.fairyland.server.Repositories;

import java.util.List;

import com.iwxyi.fairyland.server.Models.ClientUpdate;

import org.springframework.data.repository.CrudRepository;

public interface ClientUpdateRepository extends CrudRepository<ClientUpdate, Long> {

    List<ClientUpdate> findByAppAndPlatformAndPeriodAndChannelAndVersionGreaterThanOrderByVersionDesc(String app, String platform, String period, String channel, int version);
}
