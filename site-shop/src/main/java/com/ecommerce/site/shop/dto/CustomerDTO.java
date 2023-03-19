package com.ecommerce.site.shop.dto;

import com.ecommerce.site.shop.model.entity.Customer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@JsonDeserialize
@NoArgsConstructor
@Setter
public class CustomerDTO {
    private Integer id;
    private String email;
    private String password;
    private boolean enabled;
    private String firstName;
    private String lastName;
    @JsonIgnore
    public String getFullName() {
        return firstName + " " + lastName;
    }
    @JsonIgnore
    public void copyFromCustomer(Customer customer) {
        this.id = customer.getId();
        this.email = customer.getEmail();
        this.password = customer.getPassword();
        this.enabled = customer.isEnabled();
        this.firstName = customer.getFirstName();
        this.lastName = customer.getLastName();
    }
}
