package com.patterncat.apm.service;

import com.google.common.io.Resources;
import com.patterncat.apm.config.ClientConfigManager;
import com.patterncat.apm.config.ClientConfigMerger;
import com.patterncat.apm.config.ClientConfigValidator;
import com.patterncat.apm.config.client.entity.ClientConfig;
import com.patterncat.apm.config.client.entity.Domain;
import com.patterncat.apm.config.client.entity.Server;
import com.patterncat.apm.config.client.transform.DefaultSaxParser;
import com.patterncat.apm.utils.Files;
import com.patterncat.apm.utils.NetworkInterfaceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Component
public class DefaultClientConfigManager implements ClientConfigManager {
    private static final String CAT_CLIENT_XML = "META-INF/cat/client.xml";

    private static final String PROPERTIES_CLIENT_XML = "META-INF/app.properties";

    private Logger logger = LoggerFactory.getLogger(DefaultClientConfigManager.class);

    private ClientConfig m_config;

    @PostConstruct
    public void initialize() {
        File configFile = new File(Resources.getResource(CAT_CLIENT_XML).getPath());
        initialize(configFile);
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

    private ClientConfig loadConfigFromEnviroment() {
        String appName = loadProjectName();

        if (appName != null) {
            ClientConfig config = new ClientConfig();

            config.addDomain(new Domain(appName));
            return config;
        }
        return null;
    }

    private ClientConfig loadConfigFromXml() {
        InputStream in = null;
        try {
            in = Thread.currentThread().getContextClassLoader().getResourceAsStream(CAT_CLIENT_XML);

            if (in == null) {
                in = this.getClass().getClassLoader().getResourceAsStream(CAT_CLIENT_XML);
            }
            if (in != null) {
                String xml = Files.forIO().readFrom(in, "utf-8");

                logger.info(String.format("Resource file(%s) found.", this.getClass().getClassLoader().getResource(CAT_CLIENT_XML)));
                return DefaultSaxParser.parse(xml);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

    private String loadProjectName() {
        String appName = null;
        InputStream in = null;
        try {
            in = Thread.currentThread().getContextClassLoader().getResourceAsStream(PROPERTIES_CLIENT_XML);

            if (in == null) {
                in = this.getClass().getClassLoader().getResourceAsStream(PROPERTIES_CLIENT_XML);
            }
            if (in != null) {
                Properties prop = new Properties();

                prop.load(in);

                appName = prop.getProperty("app.name");
                if (appName != null) {
                    logger.info(String.format("Find domain name %s from app.properties.", appName));
                } else {
                    logger.info(String.format("Can't find app.name from app.properties."));
                    return null;
                }
            } else {
                logger.info(String.format("Can't find app.properties in %s", PROPERTIES_CLIENT_XML));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                }
            }
        }
        return appName;
    }

    @Override
    public void initialize(File configFile) {
        try {
            ClientConfig globalConfig = null;
            ClientConfig clientConfig = null;

            if (configFile != null) {
                if (configFile.exists()) {
                    String xml = Files.forIO().readFrom(configFile.getCanonicalFile(), "utf-8");

                    globalConfig = DefaultSaxParser.parse(xml);
                    logger.info(String.format("Global config file(%s) found.", configFile));
                } else {
                    logger.warn(String.format("Global config file(%s) not found, IGNORED.", configFile));
                }
            }

            // load the client configure from Java class-path
            clientConfig = loadConfigFromEnviroment();

            if (clientConfig == null) {
                clientConfig = loadConfigFromXml();
            }
            // merge the two configures together to make it effected
            if (globalConfig != null && clientConfig != null) {
                globalConfig.accept(new ClientConfigMerger(clientConfig));
            }

            if (clientConfig != null) {
                clientConfig.accept(new ClientConfigValidator());
            }

            m_config = clientConfig;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
