package com.bzj.index.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {

    @Bean
    public TomcatServletWebServerFactory tomcatEmbedded() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        // -1 表示不限制 POST 请求大小，由 Spring 来控制
        tomcat.addConnectorCustomizers(connector -> connector.setMaxPostSize(-1));
        return tomcat;
    }
}

