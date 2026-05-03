package com.n11bootcamp.search_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

        /*@Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {

            String path = System.getProperty("user.dir") + "/images/products/";

            registry.addResourceHandler("/images/products/**")
                    .addResourceLocations("file:" + path);
        }*/
}
