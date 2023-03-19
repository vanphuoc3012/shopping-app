package com.ecommerce.site.shop.model.entity;

import com.ecommerce.site.shop.model.AbstractAddressWithCountry;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;


@Entity
@Table(name = "addresses")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize
public class Address extends AbstractAddressWithCountry implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "addresses_gen")
    @TableGenerator(name = "addresses_gen",
            table = "sequencers",
            pkColumnName = "seq_name",
            valueColumnName = "seq_count",
            pkColumnValue = "addresses_seq_next_val",
            allocationSize = 1)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "customer_id", foreignKey = @ForeignKey(name = "fk_addresses_customers"))
    private Customer customer;

    @Column(name = "default_address")
    private boolean defaultForShipping;

    @JsonIgnore
    public void setDefaultAddress(boolean b) {
        this.defaultForShipping = b;
    }
}
