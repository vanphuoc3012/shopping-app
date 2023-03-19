package com.ecommerce.site.shop.controller;

import com.ecommerce.site.shop.dto.CheckOutInfo;
import com.ecommerce.site.shop.dto.EmailDTO;
import com.ecommerce.site.shop.exception.CustomerNotFoundException;
import com.ecommerce.site.shop.exception.PayPalApiException;
import com.ecommerce.site.shop.model.entity.*;
import com.ecommerce.site.shop.model.enums.PaymentMethod;
import com.ecommerce.site.shop.paypal.PayPalService;
import com.ecommerce.site.shop.service.*;
import com.ecommerce.site.shop.utils.CurrencySettingBag;
import com.ecommerce.site.shop.utils.EmailSettingBagUtils;
import com.ecommerce.site.shop.utils.PaymentSettingBag;
import com.ecommerce.site.shop.utils.Utility;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpClientErrorException;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@Controller
public class CheckOutController {

    @Autowired
    private CheckOutService checkOutService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private ShippingRateService shippingRateService;
    @Autowired
    private ShoppingCartService cartService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private SettingService settingService;
    @Autowired
    private PayPalService payPalService;
    @Value("${site.base.url}")
    private String baseUrl;
    @Autowired
    private EmailService emailService;

    @GetMapping("/checkout")
    public String showCheckOutPage(ModelMap model,
                                   HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);
        List<CartItem> cartItems = cartService.listCartItems(customer);
        CurrencySettingBag currencySettingBag = settingService.getCurrencySettingBag();

        boolean usePrimaryAddressAsDefault = addressService.usePrimaryAsDefaultAddress(customer);
        Optional<ShippingRate> optShippingRate;
        if(usePrimaryAddressAsDefault) {
            model.put("shippingAddress", customer.toString());
            optShippingRate = shippingRateService.getShippingRateForCustomer(customer);
        } else {
            Address defaultAddress = addressService.getDefaultAddress(customer);
            model.put("shippingAddress", defaultAddress.toString());
            optShippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
        }
        if(optShippingRate.isEmpty()) {
            return "redirect:" + baseUrl + "cart";
        }
        CheckOutInfo checkOutInfo = checkOutService.prepareCheckOut(cartItems, optShippingRate.get());
        String currencyCode = settingService.getCurrencyCode();
        PaymentSettingBag paymentSettingBag = settingService.getPaymentSettingBag();
        String paypalClientID = paymentSettingBag.getClientID();

        model.put("paypalClientID", paypalClientID);
        model.put("customer", customer);
        model.put("currencyCode", currencyCode);
        model.put("checkOutInfo", checkOutInfo);
        model.put("cartItems", cartItems);
        model.put("paymentTotal", Utility.formatCurrencyForPayPal(checkOutInfo.getPaymentTotal(), currencySettingBag));
        return "checkout/checkout";
    }

    @PostMapping("/place_order")
    public String placeOrder(HttpServletRequest request,
                             ModelMap model) {
        String payment = request.getParameter("paymentMethod");
        PaymentMethod paymentMethod = PaymentMethod.valueOf(payment);
        Customer customer = getAuthenticatedCustomer(request);
        List<CartItem> cartItems = cartService.listCartItems(customer);
        boolean usePrimaryAddressAsDefault = addressService.usePrimaryAsDefaultAddress(customer);
        Optional<ShippingRate> optShippingRate;
        Address defaultAddress = addressService.getDefaultAddress(customer);

        if(usePrimaryAddressAsDefault) {
            optShippingRate = shippingRateService.getShippingRateForCustomer(customer);
        } else {
            optShippingRate = shippingRateService.getShippingRateForAddress(defaultAddress);
        }

        CheckOutInfo checkOutInfo = checkOutService.prepareCheckOut(cartItems, optShippingRate.get());
        Order order = orderService.createOrder(customer, defaultAddress, cartItems, paymentMethod, checkOutInfo);
        try {
            sendOrderConfirmationEmail(request, customer, order);
        } catch (MessagingException | UnsupportedEncodingException e) {
            model.put("pageTitle", "Checkout result");
            model.put("error", true);
            model.put("message", "An error has been occur, please try again later.");
            return "checkout/checkout_completed";
        }
        cartService.deleteByCustomer(customer);

        model.put("pageTitle", "Checkout result");
        model.put("error", false);
        model.put("message", "Your Order has been completed!");
        return "checkout/checkout_completed";
    }

    @PostMapping("/process_paypal_order")
    public String processPayPalOrder(HttpServletRequest request, ModelMap model) {
        String orderId = request.getParameter("orderId");
        String pageTitle = "Checkout result";
        String message;
        try {
            if(payPalService.validateOrder(orderId)) {
                return placeOrder(request, model);
            } else {
                message = "ERROR: Transaction could not be completed because order information is invalid";
            }
        } catch (PayPalApiException | HttpClientErrorException e) {
            message = "Transaction failed due to error: " + e.getMessage();
        }
        model.put("error", true);
        model.put("pageTitle", pageTitle);
        model.put("message", message);
        return "checkout/checkout_completed";
    }

    private void sendOrderConfirmationEmail(HttpServletRequest request,
                                            Customer customer,
                                            Order order) throws MessagingException, UnsupportedEncodingException {
        EmailSettingBagUtils settingBagUtils = settingService.getEmailSettings();
        CurrencySettingBag currencySettingBag = settingService.getCurrencySettingBag();

        String customerEmail = customer.getEmail();
        String subject = settingBagUtils.getOrderConfirmSubject();
        String content = settingBagUtils.getOrderConfirmContent();

        content = content.replace("[[name]]", customer.getFullName());
        content = StringUtils.replace(content, "[[orderId]]", String.valueOf(order.getId()));
        content = StringUtils.replace(content, "[[orderTime]]", new SimpleDateFormat("E, dd MMM yyyy").format(order.getOrderTime()));
        content = StringUtils.replace(content, "[[shippingAddress]]", order.getShippingAddress());
        content = StringUtils.replace(content, "[[total]]", Utility.formatCurrency(order.getTotal(), currencySettingBag));
        content = StringUtils.replace(content, "[[paymentMethod]]", order.getPaymentMethod().toString());

        String link = baseUrl + "/orders?id=" + order.getId();
        content = StringUtils.replace(content, "[[orderLink]]", link);

        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setToEmail(customerEmail);
        emailDTO.setSubject(subject);
        emailDTO.setContent(content);
        emailDTO.setHtml(true);

        emailService.sendEmail(emailDTO);
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        return customerService.findCustomerByEmail(email).orElseThrow(
                () -> new CustomerNotFoundException("Customer not found")
        );
    }
}
