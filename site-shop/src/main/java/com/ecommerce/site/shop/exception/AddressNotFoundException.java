package com.ecommerce.site.shop.exception;

public class AddressNotFoundException extends RuntimeException {
    public AddressNotFoundException(String addressNotFound) {
        super(addressNotFound);
    }
}
