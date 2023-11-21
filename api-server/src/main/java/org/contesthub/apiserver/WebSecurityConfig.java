package org.contesthub.apiserver;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

@Component
@EnableWebSecurity
public class WebSecurityConfig{

    public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception{
//        http.sessionManagement()

        return http.build();
    }
}
