package com.iwxyi.fairyland.server.Services;

import java.util.List;

import com.iwxyi.fairyland.server.Models.ClientUpdate;
import com.iwxyi.fairyland.server.Repositories.ClientUpdateRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientUpdateService {
    @Autowired
    ClientUpdateRepository clientUpdateRepository;
    
    public List<ClientUpdate> getUpdatedVersions(String app, String platform, String period, String channel, Integer version) {
        List<ClientUpdate> clientUpdates = clientUpdateRepository.findByAppAndPlatformAndPeriodAndChannelAndVersionGreaterThanOrderByVersionDesc(app, platform, period, channel, version);
        return clientUpdates;
    }
}
