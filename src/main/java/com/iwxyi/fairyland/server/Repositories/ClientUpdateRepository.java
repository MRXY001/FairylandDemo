package com.iwxyi.fairyland.server.Repositories;

import java.util.List;

import com.iwxyi.fairyland.server.Models.ClientUpdate;

import org.springframework.data.repository.CrudRepository;

public interface ClientUpdateRepository extends CrudRepository<ClientUpdate, Long> {

    ClientUpdate findByAppAndPlatformAndVersion(String app, String platform, Long version);

    ClientUpdate findFirstByAppAndPlatformOrderByVersionDesc(String app, String platform);

    List<ClientUpdate> findByAppAndPlatformAndVersionGreaterThanOrderByVersionDesc(String app, String platform, int version);
}
