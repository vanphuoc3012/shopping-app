package com.ecommerce.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class CustomPredicatesConfig {
    public AuthenticatedCustomerRoutePredicateFactory authenticatedCustomerRoutePredicateFactory() {
        return new AuthenticatedCustomerRoutePredicateFactory();
    }
}
