package com.library.security;

import com.library.ui.views.Login;
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.with(VaadinSecurityConfigurer.vaadin(), configurer -> {
            configurer.loginView(Login.class);
        });
        return http.build();
    }

    @Bean
    public UserDetailsManager userDetailsService() {
        UserDetails user = User.withUsername("user")
                .password("{noop}user")
                .roles(Roles.USER)
                .build();
        UserDetails admin = User.withUsername("admin")
                .password("{noop}admin")
                .roles(Roles.ADMIN)
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }
}
