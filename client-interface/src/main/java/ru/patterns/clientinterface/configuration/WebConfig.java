package ru.patterns.clientinterface.configuration;

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
                .addResourceLocations("classpath:/static/client/firebase-messaging-sw.js")
                .setCachePeriod(0)
                .setCacheControl(CacheControl.noCache().mustRevalidate()
                        .sMaxAge(Duration.ofDays(0)).cachePrivate().proxyRevalidate());
    }
}
