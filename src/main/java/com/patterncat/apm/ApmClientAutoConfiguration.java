package com.patterncat.apm;

import com.patterncat.apm.config.ClientConfigManager;
import com.patterncat.apm.message.MessageProducer;
import com.patterncat.apm.message.io.TransportManager;
import com.patterncat.apm.message.spi.MessageCodec;
import com.patterncat.apm.message.spi.MessageManager;
import com.patterncat.apm.message.spi.MessageStatistics;
import com.patterncat.apm.service.*;
import com.patterncat.apm.spring.CatSpringProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by gf.crm on 2016-09-12.
 */
@Configuration
@ConditionalOnProperty(name = "apm.enabled",havingValue = "true",matchIfMissing = false)
@EnableConfigurationProperties(CatSpringProperties.class)
public class ApmClientAutoConfiguration {

    @Bean
    public CatClientTask catClientTask(){
        return new CatClientTask();
    }

    @Bean
    public ClientConfigManager clientConfigManager(){
        return new DefaultClientConfigManager();
    }

    @Bean
    public MessageManager messageManager(){
        return new DefaultMessageManager();
    }

    @Bean
    public MessageProducer messageProducer(){
        return new DefaultMessageProducer();
    }

    @Bean
    public MessageStatistics messageStatistics(){
        return new DefaultMessageStatistics();
    }

    @Bean
    public TransportManager transportManager(){
        return new DefaultTransportManager();
    }

    @Bean
    public MessageIdFactory messageIdFactory(){
        return new MessageIdFactory();
    }

    @Bean
    public MessageCodec messageCodec(){
        return new PlainTextMessageCodec();
    }

    @Bean
    public TcpSocketSender tcpSocketSender(){
        return new TcpSocketSender();
    }

    @Bean
    public StatusUpdateTask statusUpdateTask(){
        return new StatusUpdateTask();
    }

    @Bean
    public ApmApplicationContextHolder apmApplicationContextHolder(){
        return new ApmApplicationContextHolder();
    }
}
