package com.bank.employeeinterface;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/employee/css/**")
                .addResourceLocations("classpath:/static/employee/css/");

        registry.addResourceHandler("/employee/js/**")
                .addResourceLocations("classpath:/static/employee/js/");
    }
}
