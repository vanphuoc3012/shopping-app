package com.ecommerce.site.shop.repository;


import com.ecommerce.site.shop.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}
