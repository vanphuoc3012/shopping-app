package com.ecommerce.site.shop.exception;

public class PayPalApiException extends RuntimeException {
    public PayPalApiException(String message) {
        super(message);
    }
}
