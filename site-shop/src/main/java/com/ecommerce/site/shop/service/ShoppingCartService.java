package com.ecommerce.site.shop.service;

import com.ecommerce.site.shop.exception.ProductNotFoundException;
import com.ecommerce.site.shop.exception.ShoppingCartException;
import com.ecommerce.site.shop.model.entity.CartItem;
import com.ecommerce.site.shop.model.entity.Customer;
import com.ecommerce.site.shop.model.entity.Product;
import com.ecommerce.site.shop.repository.CartItemRepository;
import com.ecommerce.site.shop.repository.ProductRepository;
import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class ShoppingCartService {
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ProductRepository productRepository;

    public Integer addProduct(Integer productId, Integer quantity, Customer customer) throws ShoppingCartException {
        Integer updatedQuantity = quantity;
        Product product = new Product(productId);
        CartItem cartItem = cartItemRepository.findByCustomer_IdAndProduct_Id(customer.getId(), productId);
        if(cartItem != null) {
            updatedQuantity = cartItem.getQuantity() + quantity;
            if(updatedQuantity > 5) {
                throw new ShoppingCartException("You can add maximum 5 items of this product into your cart! You already have "
                        + cartItem.getQuantity() + " items of this product in your cart");
            }
        } else {
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setCustomer(customer);

        }
        log.info("Adding {} products into cart of '{}'", updatedQuantity, customer.getEmail());
        cartItem.setQuantity(updatedQuantity);
        cartItemRepository.save(cartItem);
        return updatedQuantity;
    }

    public List<CartItem> listCartItems(Customer customer) {
        return cartItemRepository.findAllByCustomer_Id(customer.getId());
    }

    public double updateQuantity(Integer productId, Integer quantity, Customer customer) throws ProductNotFoundException {
        cartItemRepository.updateQuantity(quantity, customer.getId(), productId);
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ProductNotFoundException("Product not found, productId: " + productId)
        );
        log.info("Customer: {}, updating product quantity", customer.getEmail());
        double subtotal = product.discountPrice() * quantity;
        return subtotal;
    }

    public void removeProduct(Customer customer, Integer productId) {
        log.info("Removing product from cart, customer {}", customer.getEmail());
        cartItemRepository.deleteByCustomer_IdAndProduct_Id(customer.getId(), productId);
    }

    public void deleteByCustomer(Customer customer) {
        cartItemRepository.deleteByCustomer(customer.getId());
    }
}
