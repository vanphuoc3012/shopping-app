package com.ecommerce.site.shop.controller;

import com.ecommerce.site.shop.exception.AddressNotFoundException;
import com.ecommerce.site.shop.exception.CustomerNotFoundException;
import com.ecommerce.site.shop.model.entity.Address;
import com.ecommerce.site.shop.model.entity.Country;
import com.ecommerce.site.shop.model.entity.Customer;
import com.ecommerce.site.shop.model.entity.State;
import com.ecommerce.site.shop.service.AddressService;
import com.ecommerce.site.shop.service.CustomerService;
import com.ecommerce.site.shop.utils.Utility;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AddressController {
    @Autowired
    private AddressService addressService;
    @Autowired
    private CustomerService customerService;
    @Value("${site.base.url}")
    private String baseUrl;

    @GetMapping("/address_book")
    public String viewAddressBook(ModelMap model,
                                  HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);
        List<Address> listAddresses = addressService.getAllCustomerAddress(customer.getId());
        boolean usePrimaryAddressAsDefault = addressService.usePrimaryAsDefaultAddress(customer);
        model.put("listAddresses", listAddresses);
        model.put("customer", customer);
        model.put("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
        return "address/addresses";
    }

    @GetMapping("/address_book/new")
    public String viewAddressForm(ModelMap model) {
        List<Country> countryList = addressService.listAllCountry();
        model.put("countryList", countryList);
        model.put("address", new Address());
        model.put("pageTitle", "Create new address");
        return "address/address_form";
    }

    @PostMapping("/address_book/save")
    public String saveNewAddress(Address address,
                                 RedirectAttributes ra,
                                 HttpServletRequest request) {
        Customer customer = getAuthenticatedCustomer(request);
        address.setCustomer(customer);
        addressService.save(address);
        ra.addFlashAttribute("message", "New address has been added");
        return "redirect:" + baseUrl + "address_book";
    }

    @GetMapping("/address_book/edit/{addressId}")
    public String editAddress(@PathVariable(name = "addressId") Integer addressId,
                              HttpServletRequest request,
                              ModelMap model,
                              RedirectAttributes ra) {
        Customer customer = getAuthenticatedCustomer(request);
        try {
            Address address = addressService.getAddressByIdAndCustomer(addressId, customer);
            model.put("address", address);

            List<Country> countryList = addressService.listAllCountry();
            model.put("countryList", countryList);

            List<State> stateList = addressService.listAllStateFromCountry(address.getCountry().getId());
            model.put("stateList", stateList);
            return "address/address_form";
        } catch (AddressNotFoundException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:" + baseUrl + "address_book";
        }
    }

    @GetMapping("/address_book/delete/{addressId}")
    public String deleteAddress(@PathVariable(name = "addressId") Integer addressId,
                                HttpServletRequest request,
                                RedirectAttributes ra) {
        Customer customer = getAuthenticatedCustomer(request);
        addressService.deleteAddressByIdAndCustomer(addressId, customer);
        ra.addFlashAttribute("message", "Address has been deleted");
        return "redirect:" + baseUrl + "address_book";
    }

    @GetMapping("/address_book/default/{addressId}")
    public String setAddressDefault(@PathVariable(name = "addressId") Integer addressId,
                                    HttpServletRequest request,
                                    RedirectAttributes ra) {
        Customer customer = getAuthenticatedCustomer(request);
        String redirect = request.getParameter("redirect");
        try {
            addressService.changeDefaultAddress(addressId, customer);
            if("cart".equals(redirect)) return "redirect:" + baseUrl + "cart";
            if("checkout".equals(redirect)) return "redirect:" + baseUrl + "checkout";
        } catch (AddressNotFoundException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:" + baseUrl + "address_book";
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        return customerService.findCustomerByEmail(email).orElseThrow(() -> new CustomerNotFoundException("Not found"));
    }
}
