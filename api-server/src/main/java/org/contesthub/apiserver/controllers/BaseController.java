package org.contesthub.apiserver.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.contesthub.apiserver.databaseInterface.DTOs.LeaderboardDto;
import org.contesthub.apiserver.databaseInterface.repositories.ContestGradingRepository;
import org.contesthub.apiserver.models.response.UserInfoResponse;
import org.contesthub.apiserver.services.UserDetailsImpl;
import org.contesthub.apiserver.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@RestController
public class BaseController {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ContestGradingRepository contestGradingRepository;

    @GetMapping("/")
    public ResponseEntity<?> index() {
        return ResponseEntity.ok("Hello World!");
    }

    @GetMapping("/anonymous")
    public ResponseEntity<?> getAnonymous() {
        return ResponseEntity.ok("Hello Anonymus!");
    }

    @GetMapping({"/profile/", "/profile/{username}"})
    public ResponseEntity<?> getUser(Principal principal, @PathVariable(required = false) String username) {
        if ((username == null || username.isBlank()) && principal != null) {
            JwtAuthenticationToken token = (JwtAuthenticationToken) principal;
            UserDetailsImpl user = userDetailsService.loadUserByToken(token);
            return ResponseEntity.ok(user);
        } else if (username.isBlank() && principal == null) {
            final Map<String, Object> body = new HashMap<>();
            body.put("status", HttpServletResponse.SC_BAD_REQUEST);
            body.put("error", "Bad Request");
            body.put("message", "Either provide a username or provide a valid token");
            body.put("path", "/profile/{username}");

            return ResponseEntity.badRequest().body(body);
        } else {
            UserDetailsImpl user = userDetailsService.loadUserByUsername(username);
            return ResponseEntity.ok(new UserInfoResponse(user));
        }
    }

    @GetMapping({"/leaderboard", "/leaderboard/{contestId}"})
    public ResponseEntity<?> getLeaderboard(@PathVariable(required = false) Integer contestId) {
        Object[][] leaderboardMatrix;
        if (contestId != null) {
            leaderboardMatrix = contestGradingRepository.getLeaderboardByContestId(contestId);
        } else {
            leaderboardMatrix = contestGradingRepository.getLeaderboard();
        }
        Set<LeaderboardDto> leaderboard = new LinkedHashSet<>();
        for(Object[] row : leaderboardMatrix) {
           leaderboard.add(new LeaderboardDto((String) row[0], Math.toIntExact((Long) row[1])));
        }
        return ResponseEntity.ok(leaderboard);
    }

    @GetMapping("/admin/introspect")
    public ResponseEntity<?> getTokenDetailsAdmin(Principal principal) {
        JwtAuthenticationToken token = (JwtAuthenticationToken) principal;
        return ResponseEntity.ok(token.getTokenAttributes());
    }
}
