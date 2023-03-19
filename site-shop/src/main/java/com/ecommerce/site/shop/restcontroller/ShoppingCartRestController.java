package com.ecommerce.site.shop.restcontroller;

import com.ecommerce.site.shop.exception.CustomerNotFoundException;
import com.ecommerce.site.shop.exception.ProductNotFoundException;
import com.ecommerce.site.shop.exception.ShoppingCartException;
import com.ecommerce.site.shop.model.entity.Customer;
import com.ecommerce.site.shop.service.CustomerService;
import com.ecommerce.site.shop.service.ShoppingCartService;
import com.ecommerce.site.shop.utils.Utility;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShoppingCartRestController {

    @Autowired
    private ShoppingCartService cartService;
    @Autowired
    private CustomerService customerService;

    @PostMapping("/cart/add/{productId}/{quantity}")
    public String addProductToCart(@PathVariable(name = "productId") Integer productId,
                                   @PathVariable(name = "quantity") Integer quantity,
                                   HttpServletRequest request) {
        try {
            Customer customer = getAuthenticatedCustomer(request);
            Integer updatedQuantity = cartService.addProduct(productId, quantity, customer);

            return updatedQuantity + " item(s) of this product were added to your shopping cart.";
        } catch (CustomerNotFoundException e) {
            return "You need login to add this product to cart.";
        } catch (ShoppingCartException e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        if(email == null) throw new CustomerNotFoundException("No authenticated customer");
        return customerService.findCustomerByEmail(email).get();
    }

    @PostMapping("/cart/update/{productId}/{quantity}")
    public String updateQuantity(@PathVariable(name = "productId") Integer productId,
                                   @PathVariable(name = "quantity") Integer quantity,
                                   HttpServletRequest request) {
        try {
            Customer customer = getAuthenticatedCustomer(request);
            double subtotal = cartService.updateQuantity(productId, quantity, customer);
            return String.valueOf(subtotal);
        } catch (CustomerNotFoundException e) {
            return "You must login to change quantity of this product";
        } catch (ProductNotFoundException e) {
            return "Product not found";
        }
    }

    @DeleteMapping("/cart/remove/{productId}")
    public String removeProduct(@PathVariable(name = "productId") Integer productId,
                                HttpServletRequest request) {
        try {
            Customer customer = getAuthenticatedCustomer(request);
            cartService.removeProduct(customer, productId);
            return "The product has been remove from your shopping cart.";
        } catch (CustomerNotFoundException e) {
            return "You must login to remove product/";
        }
    }
}
