package com.ecommerce.site.shop.service;


import com.ecommerce.site.shop.dto.EmailDTO;
import com.ecommerce.site.shop.exception.CustomerNotFoundException;
import com.ecommerce.site.shop.exception.UserNotFoundException;
import com.ecommerce.site.shop.model.entity.Country;
import com.ecommerce.site.shop.model.entity.Customer;
import com.ecommerce.site.shop.model.entity.State;
import com.ecommerce.site.shop.model.enums.AuthenticationType;
import com.ecommerce.site.shop.repository.CountryRepository;
import com.ecommerce.site.shop.repository.CustomerRepository;
import com.ecommerce.site.shop.repository.StateRepository;
import com.ecommerce.site.shop.utils.EmailSettingBagUtils;
import com.ecommerce.site.shop.utils.EmailUtils;
import io.micrometer.observation.annotation.Observed;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(rollbackOn = Exception.class)
@Slf4j
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private StateRepository stateRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SettingService settingService;
    @Autowired
    private EmailService emailService;
    @Value("${site.base.url}")
    private String baseUrl;

    public List<Country> findAllCountry() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    public List<State> findStateByCountry(Country country) {
        return stateRepository.findByCountryOrderByNameAsc(country);
    }

    public String checkEmail(String email) {
        log.info("Checking email: {}", email);
        Optional<Customer> optCustomer = customerRepository.findByEmail(email);
        if (optCustomer.isPresent()) {
            return "Duplicated";
        }
        return "OK";
    }

    public Optional<Customer> findCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public void registerCustomer(Customer customer, HttpServletRequest request) {
        log.info("Register new customer: {}", customer.getEmail());
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customer.setEnabled(false);
        customer.setCreatedTime(new Date());

        String verifyCode = RandomStringUtils.random(64, true, true);
        customer.setVerificationCode(verifyCode);
        customer.setAuthenticationType(AuthenticationType.DATABASE);

        sendVerifyEmail(customer, request);
        customerRepository.save(customer);
    }

    private void sendVerifyEmail(Customer customer, HttpServletRequest request) {
        EmailSettingBagUtils emailSettings = settingService.getEmailSettings();

        String receiver = customer.getEmail();
        String subject = emailSettings.getVerifySubject();

        String content = emailSettings.getVerifyContent();
        content = content.replace("[[name]]", customer.getFullName());
        String verifyUrl = baseUrl  + "/customers/verify/" + customer.getVerificationCode();
        content = content.replace("[[URL]]", verifyUrl);

        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setToEmail(receiver);
        emailDTO.setSubject(subject);
        emailDTO.setContent(content);
        emailDTO.setHtml(true);

        emailService.sendEmail(emailDTO);
    }

    public boolean verifyCustomer(String verifyCode) {
        Optional<Customer> optionalCustomer = customerRepository.findByVerificationCode(verifyCode);
        if (optionalCustomer.isPresent()) {
            customerRepository.updateEnabledStatus(optionalCustomer.get().getId(), true);
            log.info("Verify email successfully, customer: {}", optionalCustomer.get().getEmail());
            return true;
        }
        log.info("Verify email failed, verify code: {}", verifyCode);
        return false;
    }

    public void updateAuthenticationType(Integer customerId, AuthenticationType type) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new CustomerNotFoundException("Customer id not found, id: " + customerId));
        if (!customer.getAuthenticationType().equals(type)) {
            log.info("Customer '{}', updating authentication type to {}", customer.getEmail(), type.name());
            customerRepository.updateAuthenticationType(customer.getId(), type);
        }
    }

    public void addNewCustomerUponOAuthLogin(String name, String email, String countryCode, AuthenticationType type) {
        Customer customer = new Customer();
        log.info("Saving customer upon OAuth2 login, email = {}, login type = {}", email, type.name());
        customer.setEmail(email);
        customer.setEnabled(true);
        customer.setCreatedTime(new Date());
        customer.setAuthenticationType(type);
        customer.setPassword(RandomStringUtils.random(16));
        customer.setPhoneNumber(" ");
        customer.setAddressLine1(" ");
        customer.setAddressLine2(" ");
        customer.setCity(" ");
        customer.setState(" ");
        customer.setPostalCode(" ");
        customer.setCountry(countryRepository.findByCode(countryCode));
        setName(customer, name);

        customerRepository.save(customer);
    }

    private void setName(Customer customer, String name) {
        String[] split = name.split(" ");
        if (split.length < 2) {
            customer.setFirstName(name);
            customer.setLastName("");
        } else {
            customer.setFirstName(split[0]);
            customer.setLastName(name.replace(split[0] + " ", ""));
        }
    }

    public void updateAccountInformation(Customer customerInForm) {
        log.info("Updating customer information, customer: {}", customerInForm.getEmail());
        Customer customerInDb = customerRepository.findById(customerInForm.getId()).get();
        if (customerInForm.getPassword() == null || customerInForm.getPassword().isEmpty()) {
            customerInForm.setPassword(customerInDb.getPassword());
        } else {
            customerInForm.setPassword(passwordEncoder.encode(customerInForm.getPassword()));
        }
        customerInForm.setVerificationCode(customerInDb.getVerificationCode());
        customerInForm.setResetPasswordToken(customerInDb.getResetPasswordToken());
        customerInForm.setCreatedTime(customerInDb.getCreatedTime());
        customerInForm.setEnabled(customerInDb.isEnabled());
        customerRepository.save(customerInForm);
    }


    public String updateResetPasswordToken(String email) throws CustomerNotFoundException {
        Optional<Customer> optionalCustomer = customerRepository.findByEmail(email);
        if (optionalCustomer.isPresent()) {
            log.info("Reset password request, customer: {}", email);
            Customer customer = optionalCustomer.get();
            String random = RandomStringUtils.random(30, true, true);
            customer.setResetPasswordToken(random);

            customerRepository.save(customer);
            return random;
        } else {
            throw new CustomerNotFoundException("Could not found any customer with email: " + email);
        }
    }

    public Customer getByResetPasswordToken(String token) throws CustomerNotFoundException {
        Optional<Customer> optionalCustomer = customerRepository.findByResetPasswordToken(token);
        if (optionalCustomer.isPresent()) {
            log.info("Found customer: '{}' by reset password token", optionalCustomer.get().getEmail());
            return optionalCustomer.get();
        }
        throw new CustomerNotFoundException("Invalid reset password token");
    }

    public void updatePassword(String token, String newPassword) throws CustomerNotFoundException {
        log.info("Updating password for token: {}", token);
        Customer customer = getByResetPasswordToken(token);
        String encodedPassword = passwordEncoder.encode(newPassword);
        customer.setPassword(encodedPassword);
        customer.setResetPasswordToken(null);

        customerRepository.save(customer);
    }

    public void setPasswordNewOAuthUser(String email, String password) throws UserNotFoundException {
        Customer customer = customerRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException("Invalid email")
        );
        customer.setPassword(passwordEncoder.encode(password));
        customerRepository.save(customer);
    }
}
