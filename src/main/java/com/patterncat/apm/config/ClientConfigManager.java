package com.patterncat.apm.config;


import com.patterncat.apm.config.client.entity.Domain;
import com.patterncat.apm.config.client.entity.Server;

import java.io.File;
import java.util.List;

public interface ClientConfigManager {

    public Domain getDomain();

    public int getMaxMessageLength();

    public String getServerConfigUrl();

    public List<Server> getServers();

    public int getTaggedTransactionCacheSize();

    public void initialize();

    public boolean isCatEnabled();

    public boolean isDumpLocked();

}