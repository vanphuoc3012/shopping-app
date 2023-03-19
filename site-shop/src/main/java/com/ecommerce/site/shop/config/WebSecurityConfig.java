package com.ecommerce.site.shop.config;

import com.ecommerce.site.shop.security.DatabaseLoginSuccessHandler;
import com.ecommerce.site.shop.security.handler.CustomAuthenticationSuccessHandler;
import com.ecommerce.site.shop.security.oauth.CustomOAuth2UserService;
import com.ecommerce.site.shop.security.oauth.OAuth2LoginSuccessHandler;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig {
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private CustomOAuth2UserService oAuthUserService;
    @Autowired
    private LogoutSuccessHandler logoutSuccessHandler;
    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;
    @Bean
    public OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler() {
        return new OAuth2LoginSuccessHandler();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider dao = new DaoAuthenticationProvider();
        dao.setPasswordEncoder(passwordEncoder());
        dao.setUserDetailsService(userDetailsService);
        return dao;
    }

    @Bean
    public SecurityFilterChain filterChain(@NotNull HttpSecurity http) throws Exception {
        http.authorizeHttpRequests()
                .requestMatchers("/customers/account_update", "/customers/account", "/cart").authenticated()
                .anyRequest().permitAll();

        http.formLogin()
                .loginPage("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(authenticationSuccessHandler())
                .permitAll()
                .failureUrl("/login?error=true")
                .and()
                .logout()
                .logoutSuccessHandler(logoutSuccessHandler);

        http.oauth2Login()
                .loginPage("/login")
                .userInfoEndpoint()
                .userService(oAuthUserService)
                .and()
                .successHandler(oAuth2LoginSuccessHandler())
                .and()
                .logout().permitAll();

        http.rememberMe()
                .key("MyRememberMeKey")
                .tokenValiditySeconds(7 * 24 * 60 * 60);

        return http.build();
    }

}
