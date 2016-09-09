package com.patterncat.apm;

import com.patterncat.apm.servlet.CatFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.RequestContextFilter;

/**
 * Created by patterncat on 2016-09-09.
 */
@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean contextFilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        CatFilter catFilter = new CatFilter();
        registrationBean.setFilter(catFilter);
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
