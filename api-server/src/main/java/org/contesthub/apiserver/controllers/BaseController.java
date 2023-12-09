package org.contesthub.apiserver.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.contesthub.apiserver.databaseInterface.DTOs.LeaderboardDto;
import org.contesthub.apiserver.databaseInterface.DTOs.UserDto;
import org.contesthub.apiserver.databaseInterface.repositories.ContestGradingRepository;
import org.contesthub.apiserver.databaseInterface.repositories.ContestRepository;
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

    @Autowired
    private ContestRepository contestRepository;

    /***
     * This endpoint returns the user's profile information. This resource is public.
     * @param principal The user's JWT token (optional)
     * @param username The username of the user whose profile is to be returned (optional)
     *                 If not provided, the profile of the user corresponding to the JWT token is returned
     * @return The user's profile information based on username or JWT token
     */
    @Operation(summary = "Get user profile", description = "Get user profile information based on username or JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Requested user profile", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Indicates unknown username and/or token", content = {@Content()})
    })
    @GetMapping({"/profile/", "/profile/{username}"})
    public ResponseEntity<?> getUser(Principal principal,
                                     @Parameter(description = "Username of a target user") @PathVariable(required = false) String username) {
        if ((username == null || username.isBlank()) && principal != null) {
            JwtAuthenticationToken token = (JwtAuthenticationToken) principal;
            UserDetailsImpl user = userDetailsService.loadUserByToken(token);
            return ResponseEntity.ok(new UserDto(user.getUser()));
        } else if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Provide either a valid username or  JWT token");
        } else {
            UserDetailsImpl user = userDetailsService.loadUserByUsername(username);
            return ResponseEntity.ok(new UserInfoResponse(user));
        }
    }

    /***
     * This endpoint returns general leaderboard or leaderboard for a specific contest
     * @param contestId The contest ID for which the leaderboard is to be returned (optional)
     *                  If not provided, the leaderboard for all contests is returned
     * @return Generated leaderboard
     */
    @Operation(summary = "Get leaderboard", description = "Get leaderboard for a specific contest or for all contests")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Leaderboard ordered by score", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = LeaderboardDto.class))
        }),
        @ApiResponse(responseCode = "404", description = "Contest not found")
    })
    @GetMapping({"/leaderboard", "/leaderboard/{contestId}"})
    public ResponseEntity<?> getLeaderboard(@Parameter(description = "Id of the contest for which the leaderboard is generated") @PathVariable(required = false) Integer contestId) {
        Object[][] leaderboardMatrix;
        if (contestId != null) {
            contestRepository.findByIdAndIsPublishedTrue(contestId).orElseThrow(() ->
                    new EntityNotFoundException("Could not find contest with id " + contestId));
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

    /*** This endpoint is for administrators to validate and introspect JWT tokens. In the same way as the app does. If you want to just see the contents of a token see <a href="https://jwt.io">JWT reference</a> instead.
     * @param principal The user's JWT token
     * @return The token's details
     */
    @Operation(summary = "Get token details", description = """
    Get details of a provided token in the same way as the app does.
    If you want to just see the contents of a token see <a href="https://jwt.io">JWT reference</a> instead.
    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token details object", content = {
                    // TODO: schema for JWT token
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))
            })
    })
    @GetMapping("/admin/introspect")
    public ResponseEntity<?> getTokenDetailsAdmin(Principal principal) {
        JwtAuthenticationToken token = (JwtAuthenticationToken) principal;
        return ResponseEntity.ok(token.getTokenAttributes());
    }
}
