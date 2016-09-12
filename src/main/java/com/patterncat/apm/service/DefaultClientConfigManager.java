package com.patterncat.apm.service;

import com.patterncat.apm.spring.CatSpringProperties;
import com.patterncat.apm.config.ClientConfigManager;
import com.patterncat.apm.config.client.entity.ClientConfig;
import com.patterncat.apm.config.client.entity.Domain;
import com.patterncat.apm.config.client.entity.Server;
import com.patterncat.apm.utils.NetworkInterfaceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DefaultClientConfigManager implements ClientConfigManager {

    private Logger logger = LoggerFactory.getLogger(DefaultClientConfigManager.class);

    @Autowired
    CatSpringProperties catSpringProperties;

    private ClientConfig m_config;

    @PostConstruct
    public void initialize() {
        m_config = catSpringProperties.toClientConfig();
    }

    public ClientConfig getM_config() {
        return m_config;
    }

    @Override
    public Domain getDomain() {
        Domain domain = null;

        if (m_config != null) {
            Map<String, Domain> domains = m_config.getDomains();

            domain = domains.isEmpty() ? null : domains.values().iterator().next();
        }

        if (domain != null) {
            return domain;
        } else {
            return new Domain("UNKNOWN").setEnabled(false);
        }
    }

    @Override
    public int getMaxMessageLength() {
        if (m_config == null) {
            return 5000;
        } else {
            return getDomain().getMaxMessageSize();
        }
    }

    @Override
    public String getServerConfigUrl() {
        if (m_config == null) {
            return null;
        } else {
            List<Server> servers = m_config.getServers();

            for (Server server : servers) {
                Integer httpPort = server.getHttpPort();

                if (httpPort == null || httpPort == 0) {
                    httpPort = 8080;
                }
                return String.format("http://%s:%d/cat/s/router?domain=%s&ip=%s&op=json", server.getIp().trim(), httpPort,
                        getDomain().getId(), NetworkInterfaceManager.INSTANCE.getLocalHostAddress());
            }
        }
        return null;
    }

    @Override
    public List<Server> getServers() {
        if (m_config == null) {
            return Collections.emptyList();
        } else {
            return m_config.getServers();
        }
    }

    @Override
    public int getTaggedTransactionCacheSize() {
        return 1024;
    }

    @Override
    public boolean isCatEnabled() {
        if (m_config == null) {
            return false;
        } else {
            return m_config.isEnabled();
        }
    }

    @Override
    public boolean isDumpLocked() {
        if (m_config == null) {
            return false;
        } else {
            return m_config.isDumpLocked();
        }
    }
}
