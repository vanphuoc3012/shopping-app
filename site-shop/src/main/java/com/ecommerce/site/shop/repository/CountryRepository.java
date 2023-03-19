package com.ecommerce.site.shop.repository;


import com.ecommerce.site.shop.model.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country, Integer> {

    List<Country> findAllByOrderByNameAsc();

    Country findByCode(String code);

}
