package com.ecommerce.site.shop.mixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class CopyOnWriteArrayListMixin<E> {
    @JsonCreator
    public CopyOnWriteArrayListMixin(@JsonProperty("lock") Object lock,
                                     @JsonProperty("array") Object[] array) {
    }
}
