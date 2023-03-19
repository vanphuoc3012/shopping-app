package com.ecommerce.site.shop.service;

import com.ecommerce.site.shop.dto.CheckOutInfo;
import com.ecommerce.site.shop.model.entity.CartItem;
import com.ecommerce.site.shop.model.entity.Product;
import com.ecommerce.site.shop.model.entity.ShippingRate;
import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CheckOutService {

    private static final int DIM_DIVISOR = 139;

    public CheckOutInfo prepareCheckOut(List<CartItem> cartItems, ShippingRate shippingRate) {
        CheckOutInfo checkOutInfo = new CheckOutInfo();
        double productCost = calculateProductCost(cartItems);
        double productTotal = calculateProductTotal(cartItems);
        double shippingCostTotal = calculateShippingCost(cartItems, shippingRate);
        double paymentTotal = productTotal + shippingCostTotal;

        log.info("Preparing checkout info");

        checkOutInfo.setProductCost(productCost);
        checkOutInfo.setProductTotal(productTotal);
        checkOutInfo.setDeliverDays(shippingRate.getDays());
        checkOutInfo.setCodSupported(shippingRate.isCodSupported());
        checkOutInfo.setShippingCostTotal(shippingCostTotal);
        checkOutInfo.setPaymentTotal(paymentTotal);

        return checkOutInfo;
    }

    private double calculateShippingCost(List<CartItem> cartItems, ShippingRate shippingRate) {
          double shippingCostTotal = 0;
          for(CartItem c : cartItems) {
              Product product = c.getProduct();
              double dimWeight = (product.getLength() * product.getWidth() * product.getHeight()) / DIM_DIVISOR;
              double finalWeight = product.getWeight() > dimWeight ? product.getWeight() : dimWeight;
              double shippingCost = finalWeight * c.getQuantity() * shippingRate.getRate();
              c.setShippingCost((float) shippingCost);

              shippingCostTotal += shippingCost;
          }
          return shippingCostTotal;
    }

    private double calculateProductTotal(List<CartItem> cartItems) {
        return cartItems.stream().mapToDouble(c -> c.getSubtotal()).sum();
    }

    private double calculateProductCost(List<CartItem> cartItems) {
        return cartItems.stream().mapToDouble(c -> c.getQuantity() * c.getProduct().getCost()).sum();
    }
}
