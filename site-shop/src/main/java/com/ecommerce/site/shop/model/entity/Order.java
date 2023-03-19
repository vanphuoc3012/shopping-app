package com.ecommerce.site.shop.model.entity;

import com.ecommerce.site.shop.model.AbstractAddress;
import com.ecommerce.site.shop.model.enums.OrderStatus;
import com.ecommerce.site.shop.model.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;


@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order extends AbstractAddress implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "orders_gen")
    @TableGenerator(name = "orders_gen",
            table = "sequencers",
            pkColumnName = "seq_name",
            valueColumnName = "seq_count",
            pkColumnValue = "orders_seq_next_val",
            allocationSize = 1
    )
    private Integer id;

    @Column(nullable = false, length = 64)
    private String country;

    private Date orderTime;

    private float shippingCost;

    private float productCost;

    private float subtotal;

    private float tax;

    private float total;

    private int deliverDays;

    private Date deliverDate;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "customer_id", foreignKey = @ForeignKey(name = "fk_orders_customers"))
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<OrderDetail> orderDetails = new HashSet<>();

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("updatedTime ASC")
    private List<OrderTrack> orderTracks = new ArrayList<>();

    public Order(Integer id, Date orderTime, float productCost, float subtotal, float total) {
        this.id = id;
        this.orderTime = orderTime;
        this.productCost = productCost;
        this.subtotal = subtotal;
        this.total = total;
    }

    public void copyAddressFromCustomer() {
        setFirstName(customer.getFirstName());
        setLastName(customer.getLastName());
        setPhoneNumber(customer.getPhoneNumber());
        setAddressLine1(customer.getAddressLine1());
        setAddressLine2(customer.getAddressLine2());
        setCity(customer.getCity());
        setCountry(customer.getCountry().getName());
        setPostalCode(customer.getPostalCode());
        setState(customer.getState());
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", subtotal=" + subtotal +
                ", paymentMethod=" + paymentMethod +
                ", status=" + status +
                ", customer=" + customer.getFullName() +
                '}';
    }

    @Transient
    public String getDestination() {
        String destination =  city + ", ";
        if (state != null && !state.isEmpty()) {
            destination += state + ", ";
        }
        destination += country;

        return destination;
    }

    public void copyShippingAddressFromAddressEntity(Address address) {
        setFirstName(address.getFirstName());
        setLastName(address.getLastName());
        setPhoneNumber(address.getPhoneNumber());
        setAddressLine1(address.getAddressLine1());
        setAddressLine2(address.getAddressLine2());
        setCity(address.getCity());
        setCountry(address.getCountry().getName());
        setPostalCode(address.getPostalCode());
        setState(address.getState());
    }

    public void addOrderDetail(OrderDetail orderDetail) {
        this.orderDetails.add(orderDetail);
    }

    public String getShippingAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(addressLine1 + ", ");
        if(checkNotNullAndNotEmpty(addressLine2)) sb.append(addressLine2+ ", ");
        if(checkNotNullAndNotEmpty(city)) sb.append(city + ", ");
        if(checkNotNullAndNotEmpty(state)) sb.append(state + ", ");
        sb.append(country);
        return sb.toString();
    }

    private boolean checkNotNullAndNotEmpty(String s) {
        return s != null && !s.isEmpty();
    }
}
