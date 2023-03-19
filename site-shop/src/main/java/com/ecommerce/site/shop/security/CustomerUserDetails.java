package com.ecommerce.site.shop.security;

import com.ecommerce.site.shop.dto.CustomerDTO;
import com.ecommerce.site.shop.model.entity.Customer;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;


@Getter
@AllArgsConstructor
@JsonDeserialize
@NoArgsConstructor
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@type")
public class CustomerUserDetails implements UserDetails {

    private CustomerDTO customer;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return customer.getPassword();
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return customer.getEmail();
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return customer.isEnabled();
    }

    @JsonIgnore
    public String getFullName() {
        return customer.getFullName();
    }
}
