package com.patterncat.apm.service;

import com.patterncat.apm.config.ClientConfigManager;
import com.patterncat.apm.config.client.entity.Server;
import com.patterncat.apm.message.io.MessageSender;
import com.patterncat.apm.message.io.TransportManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

@Component
public class DefaultTransportManager implements TransportManager {

    @Autowired
    private ClientConfigManager m_configManager;

    @Autowired
    private TcpSocketSender m_tcpSocketSender;

    private Logger m_logger = LoggerFactory.getLogger(DefaultTransportManager.class);

    @Override
    public MessageSender getSender() {
        return m_tcpSocketSender;
    }

    @PostConstruct
    public void initialize() {
        List<Server> servers = m_configManager.getServers();

        if (!m_configManager.isCatEnabled()) {
            m_tcpSocketSender = null;
            m_logger.warn("CAT was DISABLED due to not initialized yet!");
        } else {
            List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>();

            for (Server server : servers) {
                if (server.isEnabled()) {
                    addresses.add(new InetSocketAddress(server.getIp(), server.getPort()));
                }
            }

            m_logger.info("Remote CAT servers: " + addresses);

            if (addresses.isEmpty()) {
                throw new RuntimeException("All servers in configuration are disabled!\r\n" + servers);
            } else {
                m_tcpSocketSender.setServerAddresses(addresses);
                m_tcpSocketSender.initialize();
            }
        }
    }

}
