package com.ecommerce.site.shop.service;

import com.ecommerce.site.shop.exception.AddressNotFoundException;
import com.ecommerce.site.shop.model.entity.Address;
import com.ecommerce.site.shop.model.entity.Country;
import com.ecommerce.site.shop.model.entity.Customer;
import com.ecommerce.site.shop.model.entity.State;
import com.ecommerce.site.shop.repository.AddressRepository;
import com.ecommerce.site.shop.repository.CountryRepository;
import com.ecommerce.site.shop.repository.StateRepository;
import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class AddressService {
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private StateRepository stateRepository;

    public List<Address> getAllCustomerAddress(Integer customerId) {
        return addressRepository.findAllByCustomer_Id(customerId);
    }

    public List<Country> listAllCountry() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    public List<State> listAllStateFromCountry(Integer countryId) {
        return stateRepository.findAllByCountry_Id(countryId);
    }

    public Address save(Address address) {
        log.info("Customer '{}' is saving new address", address.getCustomer().getEmail());
        return addressRepository.save(address);
    }

    public Address getAddressByIdAndCustomer(Integer addressId, Customer customer) {
        return addressRepository.findByIdAndCustomer_Id(addressId, customer.getId()).orElseThrow(
                () -> new AddressNotFoundException("Address not found")
        );
    }

    public void deleteAddressByIdAndCustomer(Integer addressId, Customer customer) {
        log.info("Deleting address {} by customer {}", addressId, customer.getEmail());
        getAddressByIdAndCustomer(addressId, customer);
        addressRepository.deleteByIdAndCustomer_Id(addressId, customer.getId());
    }


    public void changeDefaultAddress(Integer addressIdToDefault, Customer customer) {
        log.info("Change default address of user: {}", customer.getFullName());
        if(addressIdToDefault == 0) {
            setDefaultAddressToFalseForAll(customer);
            return;
        }
        Address address = getAddressByIdAndCustomer(addressIdToDefault, customer);
        setDefaultAddressToFalseForAll(customer);
        address.setDefaultAddress(true);
        addressRepository.save(address);
    }

    private void setDefaultAddressToFalseForAll(Customer customer) {
        List<Address> allCustomerAddress = getAllCustomerAddress(customer.getId());
        allCustomerAddress.forEach(a -> a.setDefaultAddress(false));
        addressRepository.saveAll(allCustomerAddress);
    }

    public Address getDefaultAddress(Customer customer) {
        return addressRepository.findByDefaultAddressAndCustomer_Id(customer.getId()).orElseThrow(
                () -> new AddressNotFoundException("Address not found")
        );
    }

    public boolean usePrimaryAsDefaultAddress(Customer customer) {
        return addressRepository.findByDefaultAddressAndCustomer_Id(customer.getId()).isEmpty();
    }
}
