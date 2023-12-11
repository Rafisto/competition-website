package org.contesthub.cdn.configs;

import org.contesthub.cdn.jwt.AuthEntryPointJwt;
import org.contesthub.cdn.jwt.JwtAccessDeniedHandler;
import org.contesthub.cdn.jwt.JwtAuthConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(requests -> requests
                                    .anyRequest().hasRole("USER")
                                    );
        http.oauth2ResourceServer(oauth2 -> {
            oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter));
            oauth2.authenticationEntryPoint(unauthorizedHandler);
            }
            );
        http.exceptionHandling(exception -> {
            exception.accessDeniedHandler(accessDeniedHandler);
        });
        return http.build();
    }
}
