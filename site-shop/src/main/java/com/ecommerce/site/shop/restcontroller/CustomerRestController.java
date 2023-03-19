package com.ecommerce.site.shop.restcontroller;

import com.ecommerce.site.shop.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomerRestController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/check_unique_email")
    public String checkEmail(@RequestParam(name = "email") String email) {

        return customerService.checkEmail(email);
    }
}
