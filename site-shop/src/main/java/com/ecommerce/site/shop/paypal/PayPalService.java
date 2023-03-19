package com.ecommerce.site.shop.paypal;

import com.ecommerce.site.shop.exception.PayPalApiException;
import com.ecommerce.site.shop.service.SettingService;
import com.ecommerce.site.shop.utils.PaymentSettingBag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class PayPalService {
    @Autowired
    private SettingService settingService;

    @Autowired
    private RestTemplate restTemplate;

    public boolean validateOrder(String orderId) throws PayPalApiException {
        ResponseEntity<PayPalOrderResponse> response = getOrderDetailsFromPayPalAPI(orderId);
        HttpStatus statusCode = (HttpStatus) response.getStatusCode();

        if(!statusCode.is2xxSuccessful()) {
            throwExceptionForNotOKResponse(statusCode);
        }
        PayPalOrderResponse body = response.getBody();
        return body.validate(orderId);
    }

    private ResponseEntity<PayPalOrderResponse> getOrderDetailsFromPayPalAPI(String orderId) throws PayPalApiException {
        PaymentSettingBag paymentSettingBag = settingService.getPaymentSettingBag();
        String baseURL = paymentSettingBag.getURL();
        String getOrderAPIURL = "/v2/checkout/orders/";
        String clientID = paymentSettingBag.getClientID();
        String clientSecret = paymentSettingBag.getClientSecret();

        String requestURL = baseURL + getOrderAPIURL + orderId;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.add("Accept-Language", "en-US");
        headers.setBasicAuth(clientID, clientSecret);

        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<PayPalOrderResponse> response;
        try {
            response = restTemplate.exchange(requestURL, HttpMethod.GET, httpEntity, PayPalOrderResponse.class);
        } catch (HttpClientErrorException e) {
            throw new PayPalApiException("Order ID not found");
        }
        return response;
    }

    private static void throwExceptionForNotOKResponse(HttpStatus statusCode) throws PayPalApiException {
        String message;
        switch (statusCode) {
            case NOT_FOUND:
                message = "Order ID not found";
                break;
            case BAD_REQUEST:
                message = "Bad request to PayPal checkout API";
                break;
            default:
                message = "Paypal returned no-OK status";
        }
        throw new PayPalApiException(message);
    }
}
