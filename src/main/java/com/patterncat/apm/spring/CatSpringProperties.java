package com.patterncat.apm.spring;

import com.patterncat.apm.config.client.entity.ClientConfig;
import com.patterncat.apm.config.client.entity.Domain;
import com.patterncat.apm.config.client.entity.Server;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Created by patterncat on 2016-09-09.
 */
@ConfigurationProperties(prefix = "cat")
public class CatSpringProperties {

    private String mode;

    private boolean enabled;

    private List<CatServer> servers;

    private ClientDomain domain;

    public CatSpringProperties() {
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<CatServer> getServers() {
        return servers;
    }

    public void setServers(List<CatServer> servers) {
        this.servers = servers;
    }

    public ClientDomain getDomain() {
        return domain;
    }

    public void setDomain(ClientDomain domain) {
        this.domain = domain;
    }

    /**
     * 转换成cat版本的ClientConfig
     * @return
     */
    public ClientConfig toClientConfig(){
        ClientConfig cfg = new ClientConfig();
        cfg.setEnabled(this.enabled);
        cfg.setMode(this.mode);
        if(servers != null || servers.size() > 0){
            for(CatServer server : servers){
                Server srv = new Server();
                srv.setEnabled(server.isEnabled());
                srv.setHttpPort(server.getHttpPort());
                srv.setIp(server.getIp());
                srv.setPort(server.getPort());
                cfg.addServer(srv);
            }
        }
        if(domain != null){
            //ip地址由运行时设置
            Domain dm = new Domain();
            dm.setEnabled(domain.isEnabled());
            dm.setId(domain.getId());
            cfg.addDomain(dm);
        }
        return cfg;
    }
}
