package org.contesthub.apiserver.security;

import org.contesthub.apiserver.security.jwt.AuthEntryPointJwt;
import org.contesthub.apiserver.security.jwt.JwtAccessDeniedHandler;
import org.contesthub.apiserver.security.jwt.JwtAuthConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

@Component
@EnableWebSecurity
public class WebSecurityConfig{
    @Autowired
    private JwtAuthConverter jwtAuthConverter;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Autowired
    private JwtAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception{
        http.authorizeHttpRequests(requests -> requests
                                    .requestMatchers("/", "/anonymous", "/profile/**", "/v3/**", "/swagger-ui.html", "/swagger-ui*/**", "/error", "/actuator/**").permitAll()
                                    .requestMatchers("/admin/**").hasRole("ADMIN")
//                                    .requestMatchers("/actuator/**").hasRole("ADMIN")
                                    .anyRequest().hasRole("USER")
                                    );
        http.oauth2ResourceServer(oauth2 -> {
            oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter));
            oauth2.authenticationEntryPoint(unauthorizedHandler);
            }
            );
//        http.addFilterAfter()
        http.exceptionHandling(exception -> {
            exception.accessDeniedHandler(accessDeniedHandler);
        });
        return http.build();
    }
}
