package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

import com.example.demo.security.UserContextFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserContextFilter userContextFilter;

    public SecurityConfig(UserContextFilter userContextFilter) {
        this.userContextFilter = userContextFilter;
    }

    /**
     * TODO after login implemented use this instead
     * addFilterBefore(userContextFilter,
     * UsernamePasswordAuthenticationFilter.class);
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(it -> it.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**/login").permitAll()
                        .anyRequest().permitAll())
                .addFilterBefore(userContextFilter, AuthorizationFilter.class);

        return http.build();
    }

}
