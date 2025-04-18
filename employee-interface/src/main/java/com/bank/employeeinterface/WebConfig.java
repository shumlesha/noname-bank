package com.bank.employeeinterface;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.time.Duration;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/firebase-messaging-sw.js")
                .addResourceLocations("classpath:/static/employee/firebase-messaging-sw.js")
                .setCachePeriod(0)
                .setCacheControl(CacheControl.noCache().mustRevalidate()
                        .sMaxAge(Duration.ofDays(0)).cachePrivate().proxyRevalidate());
        registry.addResourceHandler("/employee/css/**")
                .addResourceLocations("classpath:/static/employee/css/");

        registry.addResourceHandler("/employee/js/**")
                .addResourceLocations("classpath:/static/employee/js/");
    }
}
