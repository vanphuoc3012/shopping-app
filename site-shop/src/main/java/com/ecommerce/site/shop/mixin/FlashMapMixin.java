package com.ecommerce.site.shop.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.MultiValueMap;

public abstract class FlashMapMixin {
    @JsonCreator
    public FlashMapMixin(@JsonProperty("targetRequestPath") String targetRequestPath,
                         @JsonProperty("targetRequestParams") MultiValueMap<String, String> targetRequestParams,
                         @JsonProperty("expirationTime")long expirationTime) {}
}
