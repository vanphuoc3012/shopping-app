package com.ecommerce.site.shop.security;

import com.ecommerce.site.shop.dto.CustomerDTO;
import com.ecommerce.site.shop.model.entity.Customer;
import com.ecommerce.site.shop.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Service
public class CustomerUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Supplier<UsernameNotFoundException> s = () -> new UsernameNotFoundException("Username not found");
        Customer customer = customerRepository.findByEmail(username).orElseThrow(s);
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.copyFromCustomer(customer);
        return new CustomerUserDetails(customerDTO);
    }

}
