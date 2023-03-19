package com.ecommerce.site.shop.utils;


import com.ecommerce.site.shop.model.SettingBag;
import com.ecommerce.site.shop.model.entity.Setting;

import java.util.List;

public class EmailSettingBagUtils extends SettingBag {

    public EmailSettingBagUtils(List<Setting> listSettings) {
        super(listSettings);
    }

    public String getHost() {
        return this.getValue("MAIL_HOST");
    }

    public String getVerifyContent() {
        return this.getValue("CUSTOMER_VERIFY_CONTENT");
    }

    public String getVerifySubject() {
        return this.getValue("CUSTOMER_VERIFY_SUBJECT");
    }

    public String getMailFromAkaSenderEmail() {
        return this.getValue("MAIL_FROM");
    }

    public String getMailPassword() {
        return this.getValue("MAIL_PASSWORD");
    }

    public int getMailPort() {
        return Integer.parseInt(this.getValue("MAIL_PORT"));
    }

    public String getSenderName() {
        return this.getValue("MAIL_SENDER_NAME");
    }

    public String getMailUsername() {
        return this.getValue("MAIL_USERNAME");
    }

    public String getSmtpAuth() {
        return this.getValue("SMTP_AUTH");
    }

    public String getSmtpSecured() {
        return this.getValue("SMTP_SECURED");
    }

    public String getOrderConfirmSubject() {
        return this.getValue("ORDER_CONFIRMATION_SUBJECT");
    }

    public String getOrderConfirmContent() {
        return this.getValue("ORDER_CONFIRMATION_CONTENT");
    }
}
