package org.contesthub.apiserver.security.jwt;

//import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;


import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Map;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    private final JwtAuthProperties properties;

    public JwtAuthConverter(JwtAuthProperties properties) {
        this.properties = properties;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                extractResourceRoles(jwt).stream()
        ).collect(Collectors.toSet());
        return new JwtAuthenticationToken(jwt, authorities, getPrincipalClaim(jwt));
    }

    private String getPrincipalClaim(Jwt jwt){
        String claimName = properties.getPrincipalAttribute();
        if (claimName == null) {
            return null;
        }
        return jwt.getClaimAsString(claimName);
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");

        if (resourceAccess == null) {
            return Collections.emptySet();
        }

        Map<String, Object> resource = (Map<String, Object>) resourceAccess.get(properties.getResourceId());
        Collection<String> resourceRoles = (Collection<String>) resource.get("roles");

        if (resourceRoles == null) {
            return Collections.emptySet();
        }

        return resourceRoles.stream().map(role -> "ROLE_" + role).map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }
}
