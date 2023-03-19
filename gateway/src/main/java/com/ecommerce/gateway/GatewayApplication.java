package com.ecommerce.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

//    @Bean
//    public RouteLocator routeLocator(RouteLocatorBuilder routeLocatorBuilder) {
//        return routeLocatorBuilder.routes()
//                .route("authenticated", predicateSpec -> predicateSpec.path("/account_details")
//                       .uri("lb://site-shop/"))
//                .route("site-shop", predicateSpec -> predicateSpec.path("/", "/**")
//                        .uri("lb://site-shop/"))
//                .build();
//    }

}
