package com.ecommerce.site.shop.utils;

import com.ecommerce.site.shop.security.oauth.CustomOAuth2User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.security.Principal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Utility {
    public static String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(),"");
    }

    public static String formatCurrency(double amount, CurrencySettingBag currencySettingBag) {
        String symbol = currencySettingBag.getCurrencySymbol();
        String position = currencySettingBag.getCurrencySymbolPosition();
        String decimalPointType = currencySettingBag.getDecimalPointType();
        String thousandsPointType = currencySettingBag.getThousandsPointType();
        int currencyDigits = currencySettingBag.getCurrencyDigits();

        StringBuilder sb = new StringBuilder("Before".equals(position) ? symbol : "");
        sb.append("###,###");
        if(currencyDigits > 0) {
            sb.append(".");
            sb.append("#".repeat(currencyDigits));
        }
        sb.append("After".equals(position) ? " " + symbol : "");

        char decimalPointSeparator = "POINT".equals(decimalPointType) ? '.' : ',';
        char thousandsPointSeparator = "POINT".equals(thousandsPointType) ? '.' : ',';
        DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
        decimalFormatSymbols.setDecimalSeparator(decimalPointSeparator);
        decimalFormatSymbols.setGroupingSeparator(thousandsPointSeparator);

        DecimalFormat formatter = new DecimalFormat(sb.toString(), decimalFormatSymbols);
        return formatter.format(amount);
    }

    public static String formatCurrencyForPayPal(double amount, CurrencySettingBag currencySettingBag) {
        String decimalPointType = currencySettingBag.getDecimalPointType();
        String thousandsPointType = currencySettingBag.getThousandsPointType();
        int currencyDigits = currencySettingBag.getCurrencyDigits();

        StringBuilder sb = new StringBuilder();
        sb.append("###,###");
        if(currencyDigits > 0) {
            sb.append(".");
            sb.append("#".repeat(currencyDigits));
        }
        char decimalPointSeparator = "POINT".equals(decimalPointType) ? '.' : ',';
        char thousandsPointSeparator = "POINT".equals(thousandsPointType) ? '.' : ',';
        DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
        decimalFormatSymbols.setDecimalSeparator(decimalPointSeparator);
        decimalFormatSymbols.setGroupingSeparator(thousandsPointSeparator);

        DecimalFormat formatter = new DecimalFormat(sb.toString(), decimalFormatSymbols);
        return formatter.format(amount);
    }

    public static String getEmailOfAuthenticatedCustomer(HttpServletRequest request) {
        Principal userPrincipal = request.getUserPrincipal();
        if(userPrincipal == null) return null;
        String customerEmail = "";
        if(userPrincipal instanceof UsernamePasswordAuthenticationToken ||
                userPrincipal instanceof RememberMeAuthenticationToken) {
            customerEmail = userPrincipal.getName();
        } else if (userPrincipal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oAuth2Token = (OAuth2AuthenticationToken) userPrincipal;
            CustomOAuth2User oAuth2User = (CustomOAuth2User) oAuth2Token.getPrincipal();
            customerEmail = oAuth2User.getEmail();
        }
        return customerEmail;
    }
}
