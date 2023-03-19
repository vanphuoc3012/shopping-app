package com.ecommerce.site.shop.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;


@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        exposeDirectory("user-photos", registry);
//        exposeDirectory("category-images", registry);
//        exposeDirectory("brand-logos", registry);
//        exposeDirectory("product-images", registry);
//        exposeDirectory("site-logo", registry);
    }

    private void exposeDirectory(String pathPattern, @NotNull ResourceHandlerRegistry registry) {
        Path path = Paths.get("./images/" + pathPattern);
        String absolutePath = path.toFile().getAbsolutePath();
        String logicalPath = pathPattern + "/**";

        registry.addResourceHandler(logicalPath).addResourceLocations("file:/" + absolutePath + "/");
    }

}
