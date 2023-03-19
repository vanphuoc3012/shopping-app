package com.ecommerce.site.shop.utils;


import com.ecommerce.site.shop.model.SettingBag;
import com.ecommerce.site.shop.model.entity.Setting;

import java.util.List;

public class PaymentSettingBag extends SettingBag {
    public PaymentSettingBag(List<Setting> listSettings) {
        super(listSettings);
    }

    public String getURL() {
        return super.getValue("PAYPAL_API_BASE_URL");
    }

    public String getClientID() {
        return super.getValue("PAYPAL_API_CLIENT_ID");
    }

    public String getClientSecret() {
        return super.getValue("PAYPAL_API_CLIENT_SECRET");
    }
}
