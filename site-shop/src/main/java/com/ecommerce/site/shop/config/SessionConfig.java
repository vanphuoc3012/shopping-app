package com.ecommerce.site.shop.config;

import com.ecommerce.site.shop.mixin.CopyOnWriteArrayListMixin;
import com.ecommerce.site.shop.mixin.FlashMapMixin;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.jackson2.CoreJackson2Module;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.web.servlet.FlashMap;

import java.util.concurrent.CopyOnWriteArrayList;

@Configuration
public class SessionConfig implements BeanClassLoaderAware {
    private ClassLoader loader;
    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.loader = classLoader;
    }
    private ObjectMapper objectMapper() {
        ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();
        mapper.registerModules(SecurityJackson2Modules.getModules(this.loader));
        mapper.registerModule(new CoreJackson2Module());
        mapper.registerModule(new JavaTimeModule());
        mapper.addMixIn(CopyOnWriteArrayList.class, CopyOnWriteArrayListMixin.class);
        mapper.addMixIn(FlashMap.class, FlashMapMixin.class);
        return mapper;
    }
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer(objectMapper());
    }
}
