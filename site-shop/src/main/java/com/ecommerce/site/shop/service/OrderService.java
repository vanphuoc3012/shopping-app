package com.ecommerce.site.shop.service;

import com.ecommerce.site.shop.dto.CheckOutInfo;
import com.ecommerce.site.shop.model.entity.*;
import com.ecommerce.site.shop.model.enums.OrderStatus;
import com.ecommerce.site.shop.model.enums.PaymentMethod;
import com.ecommerce.site.shop.repository.OrderRepository;
import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    public Order createOrder(Customer customer,
                             Address address,
                             List<CartItem> cartItemList,
                             PaymentMethod paymentMethod,
                             CheckOutInfo checkOutInfo) {
        log.info("Customer '{}' is creating new order", customer.getEmail());
        Order newOrder = new Order();
        newOrder.setOrderTime(new Date());
        if(paymentMethod.equals(PaymentMethod.PAYPAL)) {
            newOrder.setStatus(OrderStatus.PAID);
        } else {
            newOrder.setStatus(OrderStatus.NEW);
        }
        newOrder.setCustomer(customer);
        newOrder.setProductCost((float) checkOutInfo.getProductCost());
        newOrder.setSubtotal((float) checkOutInfo.getProductTotal());
        newOrder.setShippingCost((float) checkOutInfo.getShippingCostTotal());
        newOrder.setTax(0.0f);
        newOrder.setTotal((float) checkOutInfo.getPaymentTotal());
        newOrder.setPaymentMethod(paymentMethod);
        newOrder.setDeliverDays(checkOutInfo.getDeliverDays());
        newOrder.setDeliverDate(checkOutInfo.getDeliverDate());

        if(address == null) {
            newOrder.copyAddressFromCustomer();
        } else {
            newOrder.copyShippingAddressFromAddressEntity(address);
        }
        for(CartItem cartItem : cartItemList) {
            Product product = cartItem.getProduct();

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(newOrder);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setUnitPrice((float) product.discountPrice());
            orderDetail.setProductCost(product.getCost());
            orderDetail.setSubtotal(cartItem.getSubtotal());
            orderDetail.setShippingCost(cartItem.getShippingCost());

            newOrder.addOrderDetail(orderDetail);
        }
        return orderRepository.save(newOrder);
    }
}
