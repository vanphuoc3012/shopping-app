package com.ecommerce.site.shop.repository;

import com.ecommerce.site.shop.model.entity.Country;
import com.ecommerce.site.shop.model.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface StateRepository extends JpaRepository<State, Integer> {

    List<State> findByCountryOrderByNameAsc(Country country);

    List<State> findAllByCountry_Id(Integer countryId);
}
