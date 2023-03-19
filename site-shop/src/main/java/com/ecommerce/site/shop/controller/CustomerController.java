package com.ecommerce.site.shop.controller;


import com.ecommerce.site.shop.dto.CustomerDTO;
import com.ecommerce.site.shop.exception.UserNotFoundException;
import com.ecommerce.site.shop.model.entity.Country;
import com.ecommerce.site.shop.model.entity.Customer;
import com.ecommerce.site.shop.model.entity.State;
import com.ecommerce.site.shop.security.CustomerUserDetails;
import com.ecommerce.site.shop.security.oauth.CustomOAuth2User;
import com.ecommerce.site.shop.service.CustomerService;
import com.ecommerce.site.shop.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class CustomerController {
    @Value("${site.base.url}")
    private String baseUrl;

    @Autowired
    private CustomerService customerService;

    @GetMapping("/register")
    public String customerRegister(@NotNull ModelMap model) {
        Customer customer = new Customer();
        List<Country> countryList = customerService.findAllCountry();
        model.put("customer", customer);
        model.put("countryList", countryList);
        model.put("pageTitle", "Registration");

        return "register/register_form";
    }

    @GetMapping("/customers/verify/{verifyCode}")
    public String verifyCustomer(@PathVariable String verifyCode, ModelMap model) {
        boolean verifyCustomer = customerService.verifyCustomer(verifyCode);

        if (verifyCustomer) {
            model.put("pageTitle", "Verify successfully");

            return "register/verify_success";
        }
        model.put("pageTitle", "Verify failed");
        return "register/verify_fail";
    }

    @PostMapping("/create_customer")
    public String createCustomer(Customer customer,
                                 HttpServletRequest request,
                                 @NotNull ModelMap model) throws MessagingException {
        model.put("pageTitle", "Congratulations!!");
        customerService.registerCustomer(customer, request);
        return "register/register_success";
    }

    @GetMapping("/account_details")
    public String accountEdit(@NotNull ModelMap model, HttpServletRequest request) {
        String email = getEmailOfAuthenticatedCustomer(request);

        Optional<Customer> customerByEmail = customerService.findCustomerByEmail(email);
        List<Country> countryList = customerService.findAllCountry();
        List<State> stateList = customerService.findStateByCountry(customerByEmail.get().getCountry());

        model.put("customer", customerByEmail.get());
        model.put("countryList", countryList);
        model.put("stateList", stateList);
        model.put("pageTitle", "Edit your account information");
        return "/customer/account_form";
    }

    @PostMapping("/update_account_details")
    public String saveUpdateAccountInformation(HttpServletRequest request, Customer customer,
                                               RedirectAttributes ra) {
        customerService.updateAccountInformation(customer);
        updateNameForAuthenticatedCustomer(request, customer);
        ra.addFlashAttribute("message", "Successfully update account information");
        return "redirect:" + baseUrl + "account_details";
    }

    @GetMapping("/set_password")
    public String setPasswordForOAuthUser(ModelMap model) {
        model.put("pageTitle", "Set your account password");
        return "register/set_password_oauth2user";
    }

    @PostMapping("/set_password")
    public String processNewPasswordForOAuthUser(HttpServletRequest request, ModelMap model) {
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) request.getUserPrincipal();
        CustomOAuth2User oAuth2User = (CustomOAuth2User) oAuth2AuthenticationToken.getPrincipal();
        String email = oAuth2User.getEmail();
        String password = request.getParameter("password");
        try {
            customerService.setPasswordNewOAuthUser(email, password);
        } catch (UserNotFoundException e) {
            model.put("pageTitle", e.getMessage());
            model.put("error", true);
            model.put("message", e.getMessage());
            return "message";
        }
        return "redirect:" + baseUrl;
    }

    private void updateNameForAuthenticatedCustomer(HttpServletRequest request, Customer customer) {
        Principal userPrincipal = request.getUserPrincipal();
        String fullName = customer.getFirstName() + " " + customer.getLastName();
        if (userPrincipal instanceof UsernamePasswordAuthenticationToken ||
                userPrincipal instanceof RememberMeAuthenticationToken) {
            CustomerUserDetails userDetails = getCustomerUserDetails(userPrincipal);

            CustomerDTO authenticatedCustomer = userDetails.getCustomer();
            authenticatedCustomer.setFirstName(customer.getFirstName());
            authenticatedCustomer.setLastName(customer.getLastName());
        } else if (userPrincipal instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken) {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) oAuth2AuthenticationToken.getPrincipal();
            oAuth2User.setFullName(fullName);
        }
    }

    private CustomerUserDetails getCustomerUserDetails(Principal principal) {
        CustomerUserDetails userDetails;
        if (principal instanceof UsernamePasswordAuthenticationToken token) {
            userDetails = (CustomerUserDetails) token.getPrincipal();
        } else {
            var token = (RememberMeAuthenticationToken) principal;
            userDetails = (CustomerUserDetails) token.getPrincipal();
        }
        return userDetails;
    }

    private String getEmailOfAuthenticatedCustomer(HttpServletRequest request) {
        Principal userPrincipal = request.getUserPrincipal();
        String email = "";
        if (userPrincipal instanceof UsernamePasswordAuthenticationToken ||
                userPrincipal instanceof RememberMeAuthenticationToken) {
            email = userPrincipal.getName();
        } else if (userPrincipal instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken) {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) oAuth2AuthenticationToken.getPrincipal();
            email = oAuth2User.getEmail();
        }
        return email;
    }
}
