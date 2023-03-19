package com.ecommerce.site.shop.service;

import com.ecommerce.site.shop.model.entity.Currency;
import com.ecommerce.site.shop.model.entity.Setting;
import com.ecommerce.site.shop.model.enums.SettingCategory;
import com.ecommerce.site.shop.repository.CurrencyRepository;
import com.ecommerce.site.shop.repository.SettingRepository;
import com.ecommerce.site.shop.utils.CurrencySettingBag;
import com.ecommerce.site.shop.utils.EmailSettingBagUtils;
import com.ecommerce.site.shop.utils.PaymentSettingBag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SettingService {

    @Autowired
    private SettingRepository settingRepository;
    @Autowired
    private CurrencyRepository currencyRepository;

    public List<Setting> getGeneralSettings() {
        return settingRepository.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
    }

    public EmailSettingBagUtils getEmailSettings() {
        List<Setting> emailSetting = settingRepository.findByCategory(SettingCategory.MAIL_SERVER);
        emailSetting.addAll(settingRepository.findByCategory(SettingCategory.MAIL_TEMPLATES));

        return new EmailSettingBagUtils(emailSetting);
    }

    public CurrencySettingBag getCurrencySettingBag() {
        return new CurrencySettingBag(settingRepository.findByCategory(SettingCategory.CURRENCY));
    }

    public String getCurrencyCode() {
        Setting setting = settingRepository.findByKey("CURRENCY_ID").get();
        Currency currency = currencyRepository.findById(Integer.valueOf(setting.getValue())).get();
        return currency.getCode();
    }

    public PaymentSettingBag getPaymentSettingBag() {
        List<Setting> settings = settingRepository.findByCategory(SettingCategory.PAYMENT);
        return new PaymentSettingBag(settings);
    }
}
