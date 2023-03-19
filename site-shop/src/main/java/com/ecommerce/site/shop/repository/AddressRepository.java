package com.ecommerce.site.shop.repository;

import com.ecommerce.site.shop.model.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Integer> {

    List<Address> findAllByCustomer_Id(Integer customerId);

    Optional<Address> findByIdAndCustomer_Id(Integer addressId, Integer id);

    void deleteByIdAndCustomer_Id(Integer addressId, Integer id);

    @Query("SELECT a FROM Address a WHERE a.customer.id = ?1 AND a.defaultForShipping = true")
    Optional<Address> findByDefaultAddressAndCustomer_Id(Integer id);
}
