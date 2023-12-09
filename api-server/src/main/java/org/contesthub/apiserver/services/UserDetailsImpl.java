package org.contesthub.apiserver.services;

import org.contesthub.apiserver.databaseInterface.DTOs.ContestDto;
import org.contesthub.apiserver.databaseInterface.DTOs.ContestGradingDto;
import org.contesthub.apiserver.databaseInterface.DTOs.GroupDto;
import org.contesthub.apiserver.databaseInterface.DTOs.UserDto;
import org.contesthub.apiserver.databaseInterface.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class UserDetailsImpl implements UserDetails {
    private Integer id;
    private String username;
    private String email;

    private Collection<? extends GrantedAuthority> authorities;

    private User user;

    public UserDetailsImpl(Integer id, String username, String email, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(UserDto user, JwtAuthenticationToken jwt) {
        return new UserDetailsImpl(user.getId(),
                                   jwt.getTokenAttributes().get("preferred_username").toString(),
                                   jwt.getTokenAttributes().get("email") != null ? jwt.getTokenAttributes().get("email").toString() : null,
                                   jwt.getAuthorities());
    }

    public static UserDetailsImpl build(User user) {
        UserDetailsImpl userDetails= new UserDetailsImpl(user.getId(),
                                                         user.getUsername(),
                                                         user.getEmail(),
                                                null);
        userDetails.user = user;
        return userDetails;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    // Those are expected by the interface, but JWT handles those cases instead

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true ;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
