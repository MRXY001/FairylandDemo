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

    public ClientUpdate getLastestVersion(String app, String platform) {
        ClientUpdate clientUpdate = clientUpdateRepository.findFirstByAppAndPlatformOrderByVersionDesc(app, platform);
        return clientUpdate;
    }
    
    public List<ClientUpdate> getUpdatedVersions(String app, String platform, Integer version) {
        List<ClientUpdate> clientUpdates = clientUpdateRepository.findByAppAndPlatformAndVersionGreaterThanOrderByVersionDesc(app, platform, version);
        return clientUpdates;
    }
}
