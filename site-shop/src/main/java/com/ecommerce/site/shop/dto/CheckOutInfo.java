package com.ecommerce.site.shop.dto;

import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

@Getter
@Setter
public class CheckOutInfo {
    private double productCost;
    private double productTotal;
    private double shippingCostTotal;
    private double paymentTotal;
    private int deliverDays;
    private boolean codSupported;

    public Date getDeliverDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, deliverDays);
        return calendar.getTime();
    }

    public String getPaymentTotalForPayPal() {
        DecimalFormat formatter = new DecimalFormat("###.##");
        return formatter.format(paymentTotal);
    }
}
