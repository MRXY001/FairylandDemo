package com.iwxyi.fairyland.server.Repositories;

import com.iwxyi.fairyland.server.Models.ClientStartup;

import org.springframework.data.repository.CrudRepository;

public interface ClientStartupRepository extends CrudRepository<ClientStartup, Long> {
    ClientStartup findByStartupId(Long startupId);
}
