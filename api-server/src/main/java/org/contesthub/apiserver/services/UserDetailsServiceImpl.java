package org.contesthub.apiserver.services;

import org.contesthub.apiserver.databaseInterface.DTOs.UserDto;
import org.contesthub.apiserver.databaseInterface.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import org.contesthub.apiserver.databaseInterface.repositories.UserRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> {
            throw new UsernameNotFoundException("User Not Found with username: " + username);
        });

        return UserDetailsImpl.build(user);
    }

    @Transactional
    public UserDetailsImpl loadUserByToken(JwtAuthenticationToken token) throws UsernameNotFoundException {
        String username = token.getTokenAttributes().get("preferred_username").toString();
        User user = userRepository.findByUsername(username).orElseGet(() -> {
            // Create user based of token if non-existent
            User tmpUser = new User(username, token.getTokenAttributes().get("email") != null ? token.getTokenAttributes().get("email").toString() : null);
            userRepository.saveAndFlush(tmpUser);
            return userRepository.findByUsername(username).orElse(null);
        });

        // This assertion should always be true, as non-existent user object is created above
        assert user != null;

        // Update email if it is not set or if it is different from the one in the token
        if (user.getEmail() == null && token.getTokenAttributes().get("email") != null) {
            user.setEmail(token.getTokenAttributes().get("email").toString());
            userRepository.saveAndFlush(user);
        } else if (user.getEmail() != null && token.getTokenAttributes().get("email") != null && !user.getEmail().equals(token.getTokenAttributes().get("email").toString())) {
            user.setEmail(token.getTokenAttributes().get("email").toString());
            userRepository.saveAndFlush(user);
        }

        return UserDetailsImpl.build(user);
    }

    @Transactional
    public Set<User> loadUsersFromIdList(Integer[] userIds) {
        return userRepository.findAllByIdIn(userIds);
    }

    @Transactional
    public User createTemporaryUser(String username) {
        User user = new User(username, null);
        userRepository.saveAndFlush(user);
        return user;
    }
}
