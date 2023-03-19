package com.ecommerce.site.shop.controller;

import com.ecommerce.site.shop.model.entity.Category;
import com.ecommerce.site.shop.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MainController {
    @Value("${site.base.url}")
    private String baseUrl;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/")
    public String viewHomePage(@NotNull Model model,
                               HttpServletRequest request) {

        List<Category> listCategories = categoryService.listNoChildrenCategories();
        model.addAttribute("listCategories", listCategories);

        return "index";
    }

    @GetMapping("/login")
    public String viewLoginPage(HttpServletRequest request) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if(authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }
        return "redirect:" + baseUrl;
    }

}
