package com.ecommerce.site.shop.security;

import com.ecommerce.site.shop.dto.CustomerDTO;
import com.ecommerce.site.shop.model.entity.Customer;
import com.ecommerce.site.shop.model.enums.AuthenticationType;
import com.ecommerce.site.shop.service.CustomerService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class DatabaseLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private CustomerService customerService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                        Authentication authentication) throws IOException, ServletException {
        CustomerUserDetails userDetails = (CustomerUserDetails) authentication.getPrincipal();
        CustomerDTO customer = userDetails.getCustomer();
        System.out.println(customer.getFullName());
        customerService.updateAuthenticationType(customer.getId(), AuthenticationType.DATABASE);

        super.onAuthenticationSuccess(request, response, chain, authentication);
    }

}
