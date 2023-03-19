package com.ecommerce.site.shop.repository;

import com.ecommerce.site.shop.model.entity.ShippingRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShippingRateRepository extends JpaRepository<ShippingRate, Integer> {
    Optional<ShippingRate> findByCountry_IdAndState(Integer countryId, String state);
}
