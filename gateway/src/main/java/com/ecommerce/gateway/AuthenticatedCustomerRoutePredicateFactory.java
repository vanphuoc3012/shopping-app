package com.ecommerce.gateway;

import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.GatewayPredicate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public class AuthenticatedCustomerRoutePredicateFactory extends
        AbstractRoutePredicateFactory<AuthenticatedCustomerRoutePredicateFactory.Config> {


    public AuthenticatedCustomerRoutePredicateFactory() {
        super(Config.class);
    }

    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        System.out.println("check check inside apply");
        return (GatewayPredicate) serverWebExchange -> {
            System.out.println("111check check inside apply");
            Mono<WebSession> session = serverWebExchange.getSession();
            System.out.println("Request id: " + serverWebExchange.getRequest().getId());
            System.out.println(session);
            return true;
        };
    }

    @Validated
    public static class Config {
        Boolean isAuthenticated;

        public boolean isAuthenticated() {
            return isAuthenticated;
        }

        public void setAuthenticated(boolean authenticated) {
            isAuthenticated = authenticated;
        }

        public Config(boolean isAuthenticated) {
            this.isAuthenticated = isAuthenticated;
        }
    }
}
