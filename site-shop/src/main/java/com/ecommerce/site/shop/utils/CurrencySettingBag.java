package com.ecommerce.site.shop.utils;

import com.ecommerce.site.shop.model.SettingBag;
import com.ecommerce.site.shop.model.entity.Setting;

import java.util.List;

public class CurrencySettingBag extends SettingBag {
    public CurrencySettingBag(List<Setting> listSettings) {
        super(listSettings);
    }

    public String getCurrencySymbol() {
        return super.getValue("CURRENCY_SYMBOL");
    }

    public String getCurrencySymbolPosition() {
        return super.getValue("CURRENCY_SYMBOL_POSITION");
    }

    public String getDecimalPointType() {
        return super.getValue("DECIMAL_POINT_TYPE");
    }

    public String getThousandsPointType() {
        return super.getValue("THOUSANDS_POINT_TYPE");
    }

    public int getCurrencyDigits() {
        return Integer.parseInt(super.getValue("DECIMAL_DIGITS"));
    }
}
