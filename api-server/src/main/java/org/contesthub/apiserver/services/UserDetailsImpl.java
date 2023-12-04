package org.contesthub.apiserver.services;

import org.contesthub.apiserver.databaseInterface.DTOs.ContestDto;
import org.contesthub.apiserver.databaseInterface.DTOs.ContestGradingDto;
import org.contesthub.apiserver.databaseInterface.DTOs.GroupDto;
import org.contesthub.apiserver.databaseInterface.DTOs.UserDto;
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

    private Set<ContestGradingDto> contestGradings = new LinkedHashSet<>();
    private Set<ContestDto> contests = new LinkedHashSet<>();
    private Set<GroupDto> groups = new LinkedHashSet<>();

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

    public static UserDetailsImpl build(UserDto user) {
        UserDetailsImpl userDetails= new UserDetailsImpl(user.getId(),
                                                         user.getUsername(),
                                                         user.getEmail(),
                                                null);
        userDetails.setContestGradings(user.getContestGradings());
        userDetails.setContests(user.getContests());
        userDetails.setGroups(user.getGroups());
        return userDetails;
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

    public Set<ContestGradingDto> getContestGradings() {
        return contestGradings;
    }

    public void setContestGradings(Set<ContestGradingDto> contestGradings) {
        this.contestGradings = contestGradings;
    }

    public Set<ContestDto> getContests() {
        return contests;
    }

    public void setContests(Set<ContestDto> contests) {
        this.contests = contests;
    }

    public Set<GroupDto> getGroups() {
        return groups;
    }

    public void setGroups(Set<GroupDto> groups) {
        this.groups = groups;
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
