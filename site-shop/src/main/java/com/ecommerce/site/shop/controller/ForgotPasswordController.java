package com.ecommerce.site.shop.controller;

import com.ecommerce.site.shop.dto.EmailDTO;
import com.ecommerce.site.shop.exception.CustomerNotFoundException;
import com.ecommerce.site.shop.model.entity.Customer;
import com.ecommerce.site.shop.service.EmailService;
import com.ecommerce.site.shop.utils.EmailSettingBagUtils;
import com.ecommerce.site.shop.utils.EmailUtils;
import com.ecommerce.site.shop.service.CustomerService;
import com.ecommerce.site.shop.service.SettingService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ForgotPasswordController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private EmailService emailService;

    @GetMapping("/forgot_password")
    public String showRequestForm(@NotNull ModelMap model) {
        model.put("pageTitle", "Forgot Password");
        return "customer/forgot_password_form";
    }

    @PostMapping("/forgot_password")
    public String processForgotPasswordRequest(@NotNull HttpServletRequest request,
                                               @NotNull ModelMap model) {
        String email = request.getParameter("email");
        try {
            String token = customerService.updateResetPasswordToken(email);
            String requestLink = EmailUtils.getSiteUrl(request) + "/reset_password?token=" + token;
            sendEmail(requestLink, email);
            model.put("message", "We have sent a request password link to your email");
        } catch (CustomerNotFoundException e) {
            model.put("error", e.getMessage());
        }
        model.put("pageTitle", "Reset Password");
        return "customer/forgot_password_form";
    }

    @GetMapping("/reset_password")
    public String showResetForm(@RequestParam(name = "token") String token,
                                @NotNull ModelMap model) {
        try {
            Customer customer = customerService.getByResetPasswordToken(token);
            model.put("pageTitle", "Reset your password");
            model.put("token", token);
            return "customer/reset_password_form";
        } catch (CustomerNotFoundException e) {
            model.put("pageTitle", "Invalid token");
            model.put("message", "Invalid token");
            model.put("error", "Error");
           return "message";
        }
    }

    @PostMapping("/reset_password")
    public String processNewPassword(HttpServletRequest request,
                                     ModelMap model) {
        String token = request.getParameter("token");
        String password = request.getParameter("password");
        try {
            customerService.updatePassword(token, password);
            model.put("pageTitle", "Successfully change your password");
            model.put("message", "You have successfully changed your password");
        } catch (CustomerNotFoundException e) {
            model.put("pageTitle", "Invalid token");
            model.put("error", "Error");
            model.put("message", e.getMessage());
        }
        return "message";
    }

    private void sendEmail(String requestLink, String toEmail) {
        String subject = "Reset your password";
        String content = """
                Hello,<div>You have requested to reset your password.</div><div>Click the link below to change your pass word:</div><div><br></div><h1><font color="#008000">

                <a href="[[URL]]" target="_blank">RESET PASSWORD</a></font></h1><a href="[[URL]]" target="_blank"></a><div></div><div><br></div><div>Thank you.</div>""";
        content = content.replace("[[URL]]", requestLink);

        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setToEmail(toEmail);
        emailDTO.setSubject(subject);
        emailDTO.setContent(content);
        emailDTO.setHtml(true);

        emailService.sendEmail(emailDTO);
    }
}
