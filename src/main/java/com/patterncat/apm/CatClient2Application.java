package com.patterncat.apm;

import com.patterncat.apm.spring.CatSpringProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
//@ConditionalOnProperty(value = "cat")
@EnableConfigurationProperties(CatSpringProperties.class)
public class CatClient2Application {

    public static void main(String[] args) {
        SpringApplication.run(CatClient2Application.class, args);
    }
}
