package org.contesthub.apiserver.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.Response;
import org.contesthub.apiserver.databaseInterface.DTOs.*;
import org.contesthub.apiserver.databaseInterface.models.*;
import org.contesthub.apiserver.databaseInterface.repositories.ContestGradingRepository;
import org.contesthub.apiserver.databaseInterface.repositories.ContestRepository;
import org.contesthub.apiserver.databaseInterface.repositories.GroupRepository;
import org.contesthub.apiserver.databaseInterface.repositories.UserRepository;
import org.contesthub.apiserver.models.response.ContestResponse;
import org.contesthub.apiserver.services.ContestService;
import org.contesthub.apiserver.services.UserDetailsImpl;
import org.contesthub.apiserver.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RestController()
@RequestMapping("/contests/")
public class ContestController {

    @Autowired
    private ContestRepository contestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContestGradingRepository contestGradingRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ContestService contestService;

    /***
     * This endpoint lists all contests in which the user can participate
     * @param joined Whether to list contests the user has joined or not (default: true)
     */
    @Operation(summary = "Get contests", description = "Get contests in which the user can participate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All contests available to user", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ContestResponse.class))
            })
    })
    @GetMapping("list")
    public ResponseEntity<?> getContests(Principal principal,
                                         @Parameter(description = "Whether to list contests the user has joined or not") @RequestParam(defaultValue = "true") Boolean joined) {
        UserDetailsImpl userDetails = userDetailsService.loadUserByToken((JwtAuthenticationToken) principal);
        List<Contest> contests = contestRepository.findByIsPublishedTrue();
        // Maybe introduce a new DTO for this instead of using generic ContestDto?
        // Mainly due to Schema containing non-existent fields now
        List<ContestResponse> contestResponse = contests.stream().map(contest -> new ContestResponse(
                new ContestDto(contest),
                contest.getIsPublished() && contest.getGroups().stream().anyMatch(group -> group.getUsers().contains(userDetails.getUser()))
        )).toList();
        return ResponseEntity.ok(contestResponse);
    }

    /***
     * This endpoint lists contest in which the user is participating
     * @param principal The user's JWT token
     */
    @Operation(summary = "Get joined contests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All contests in which user participates", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ContestDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "User is not participating in any contests")
    })
    @GetMapping("list/joined")
    public ResponseEntity<?> getUserContestList(Principal principal) {
        JwtAuthenticationToken token = (JwtAuthenticationToken) principal;
        UserDetails user = userDetailsService.loadUserByToken(token);
        List<ContestDto> contests = contestRepository.findByUsersContainsAndIsPublishedTrue(
                userRepository.findByUsername(user.getUsername()).orElseThrow(null)).stream()
                .map(contest -> new ContestDto(contest.getId(), contest.getTitle(), contest.getDescription(), contest.getIsPublished()
        )).toList();
        if (contests.isEmpty()) {
            throw new EntityNotFoundException("You're not participating in any contests");
        }
        return ResponseEntity.ok(contests);
    }

    /***
     * This endpoint lists the leaderboard of a contest
     * @param principal The user's JWT token
     */
    @Operation(summary = "Get contest leaderboard", description = "Get leaderboard for a specific contest, accessible only to participants")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Leaderboard ordered by score", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = LeaderboardDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Contest not found")
    })
    @GetMapping("{contestId}/leaderboard")
    public ResponseEntity<?> getContestLeaderboard(Principal principal,
                                                   @Parameter(description = "Id of a contest for which the leaderboard will be returned") @PathVariable Integer contestId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/leaderboard/" + contestId);
        return new ResponseEntity<String>(headers, HttpStatus.FOUND);
    }

    /***
     * This endpoint allows a user to join a contest
     * @param principal The user's JWT token
     * @return Contest object with updated user list
     */
    @Operation(summary = "Join a contest")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contest object with updated user list", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ContestDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Contest not found")
    })
    @PostMapping("{contestId}/join")
    public ResponseEntity<?> joinContest(Principal principal,
                                         @Parameter(description = "Id of a contest you want to join") @PathVariable Integer contestId) {
        JwtAuthenticationToken token = (JwtAuthenticationToken) principal;
        UserDetailsImpl user = userDetailsService.loadUserByToken(token);
        // TODO - Joining a contest should require permission
        Contest contest = contestService.loadContestsUserCanJoin(user).stream()
                .filter(contestDto -> contestDto.getId().equals(contestId)).findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Contest not found, maybe you don't have permission to join it?"));
        // Don't throw as it will never be empty - loadUserByToken() will populate db if needed
        contest.getUsers().add(userRepository.findByUsername(user.getUsername()).orElseThrow(null));
        contestRepository.save(contest);
        return ResponseEntity.ok(new ContestDto(contest));
    }

    // TODO: New endpoints for review
    @Operation(summary = "Get problem statements for a contest")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of problem statements for a contest", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ContestDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Contest not found")
    })
    @GetMapping("{contestId}/problems")
    public ResponseEntity<?> getContestProblems(Principal principal,
                                                @Parameter(description = "Id of a contest for which the problems will be returned") @PathVariable Integer contestId) {
        UserDetailsImpl userDetails = userDetailsService.loadUserByToken((JwtAuthenticationToken) principal);
        // Possibly add a DTO for this?
        List<ContestProblemDto> contestProblems = contestRepository.findByIdAndUsersContainsAndIsPublishedTrue(contestId, userDetails.getUser())
                .orElseThrow(
                        () -> new EntityNotFoundException("Contest Not found")
                ).getContestProblems().stream().map(contestProblem ->
                        new ContestProblemDto(contestProblem.getId(), contestProblem.getTitle(), contestProblem.getContents(), contestProblem.getUseAutograding(), contestProblem.getUseAutogradingAnswer(), contestProblem.getDeadline())
                ).toList();
        return ResponseEntity.ok(contestProblems);
    }

    @Operation(summary = "Get user's submissions for a contest")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of user's submissions for a contest", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ContestGradingDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Contest not found")
    })
    @GetMapping("{contestId}/submissions")
    public ResponseEntity<?> getContestSubmissions(Principal principal,
                                                   @Parameter(description = "Id of a contest for which the submissions will be returned") @PathVariable Integer contestId) {
        UserDetailsImpl userDetails = userDetailsService.loadUserByToken((JwtAuthenticationToken) principal);
        // Possibly add a DTO for this?
        Set<ContestProblem> contestProblems = contestRepository.findByIdAndUsersContainsAndIsPublishedTrue(contestId, userDetails.getUser())
                .orElseThrow(
                        () -> new EntityNotFoundException("Contest Not found")
                ).getContestProblems();

        List<ContestGrading> contestGradings = contestProblems.stream().map(ContestProblem::getContestGradings).reduce(new LinkedHashSet<ContestGrading>(), (reducedSet, newSet) -> {
            reducedSet.addAll(newSet);
            return reducedSet;
        }).stream().filter(contestGrading -> contestGrading.getUser().equals(userDetails.getUser())).toList();
        return ResponseEntity.ok(contestGradings.stream().map(ContestGradingDto::new).toList());
    }

    @Operation(summary = "Get user's score for a contest")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User's score for a contest", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = LeaderboardDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "Contest not found")
    })
    @GetMapping("{contestId}/score")
    public ResponseEntity<?> getContestScore(Principal principal, @Parameter(description = "Id of a contest for which the score will be returned") @PathVariable Integer contestId) {
        UserDetailsImpl userDetails = userDetailsService.loadUserByToken((JwtAuthenticationToken) principal);
        // Possibly add a DTO for this?
        Object[][] leaderboardMatrix = contestGradingRepository.getLeaderboardByContestId(contestId);
        Set<LeaderboardDto> leaderboard = new LinkedHashSet<>();
        for(Object[] row : leaderboardMatrix) {
            leaderboard.add(new LeaderboardDto((String) row[0], Math.toIntExact((Long) row[1])));
        }
        return ResponseEntity.ok(leaderboard.stream().filter(leaderboardDto -> leaderboardDto.getUsername().equals(userDetails.getUser().getUsername())).findFirst().orElseThrow(() -> new EntityNotFoundException("Contest Not found")));
    }
}
