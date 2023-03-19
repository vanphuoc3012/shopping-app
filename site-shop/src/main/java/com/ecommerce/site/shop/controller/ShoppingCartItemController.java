package com.ecommerce.site.shop.controller;


import com.ecommerce.site.shop.exception.CustomerNotFoundException;
import com.ecommerce.site.shop.model.entity.Address;
import com.ecommerce.site.shop.model.entity.CartItem;
import com.ecommerce.site.shop.model.entity.Customer;
import com.ecommerce.site.shop.model.entity.ShippingRate;
import com.ecommerce.site.shop.service.AddressService;
import com.ecommerce.site.shop.service.CustomerService;
import com.ecommerce.site.shop.service.ShippingRateService;
import com.ecommerce.site.shop.service.ShoppingCartService;
import com.ecommerce.site.shop.utils.Utility;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class ShoppingCartItemController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ShoppingCartService cartService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private ShippingRateService shippingRateService;

    @GetMapping("/cart")
    public String viewCart(ModelMap model,
                           HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);
        List<CartItem> cartItems = cartService.listCartItems(customer);
        double estimatedTotal = cartItems.stream()
                .mapToDouble(c -> c.getSubtotal())
                .sum();
        boolean usePrimaryAddressAsDefault = addressService.usePrimaryAsDefaultAddress(customer);
        Optional<ShippingRate> optShippingRate;
        if(usePrimaryAddressAsDefault) {
            optShippingRate = shippingRateService.getShippingRateForCustomer(customer);
        } else {
            Address defaultAddress = addressService.getDefaultAddress(customer);
            optShippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
        }

        model.put("shippingSupported", optShippingRate.isPresent());
        model.put("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
        model.put("cartItems", cartItems);
        model.put("estimatedTotal", estimatedTotal);
        return "cart/shopping_cart";
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        return customerService.findCustomerByEmail(email).orElseThrow(
                () -> new CustomerNotFoundException("Customer not found")
        );
    }
}
