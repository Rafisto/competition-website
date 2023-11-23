package org.contesthub.apiserver.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class AuthController {
    @GetMapping("/")
    public ResponseEntity<?> index() {
        return ResponseEntity.ok("Hello World!");
    }

    @GetMapping("/anonymus")
    public ResponseEntity<?> getAnonymus() {
        return ResponseEntity.ok("Hello Anonymus!");
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUser(Principal principal) {
        JwtAuthenticationToken token = (JwtAuthenticationToken) principal;
        String username = (String) token.getTokenAttributes().get("preferred_username");
        String email = (String) token.getTokenAttributes().get("email");
        return ResponseEntity.ok("Received user token for " + username + " with email " + email);
    }

    @GetMapping("/admin")
    public ResponseEntity<?> getAdmin(Principal principal) {
        JwtAuthenticationToken token = (JwtAuthenticationToken) principal;
        String username = (String) token.getTokenAttributes().get("preferred_username");
        String email = (String) token.getTokenAttributes().get("email");
        return ResponseEntity.ok("Received admin token for " + username + " with email " + email);
    }

    @GetMapping("/user/introspect")
    public ResponseEntity<?> getTokenDetails(Principal principal) {
        JwtAuthenticationToken token = (JwtAuthenticationToken) principal;
        return ResponseEntity.ok(token.getTokenAttributes());
    }

    @GetMapping("/admin/introspect")
    public ResponseEntity<?> getTokenDetailsAdmin(Principal principal) {
        JwtAuthenticationToken token = (JwtAuthenticationToken) principal;
        return ResponseEntity.ok(token.getTokenAttributes());
    }
}
