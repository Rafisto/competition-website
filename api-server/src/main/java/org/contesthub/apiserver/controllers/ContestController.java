package org.contesthub.apiserver.controllers;

import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.Response;
import org.contesthub.apiserver.databaseInterface.DTOs.ContestDto;
import org.contesthub.apiserver.databaseInterface.DTOs.GroupDto;
import org.contesthub.apiserver.databaseInterface.DTOs.LeaderboardDto;
import org.contesthub.apiserver.databaseInterface.models.Contest;
import org.contesthub.apiserver.databaseInterface.models.Group;
import org.contesthub.apiserver.databaseInterface.models.User;
import org.contesthub.apiserver.databaseInterface.repositories.ContestGradingRepository;
import org.contesthub.apiserver.databaseInterface.repositories.ContestRepository;
import org.contesthub.apiserver.databaseInterface.repositories.GroupRepository;
import org.contesthub.apiserver.databaseInterface.repositories.UserRepository;
import org.contesthub.apiserver.services.ContestService;
import org.contesthub.apiserver.services.UserDetailsImpl;
import org.contesthub.apiserver.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RestController("/contests")
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
     * @param principal The user's JWT token
     * @param joined Whether to list contests the user has joined or not (default: true)
     */
    @GetMapping("/list")
    public ResponseEntity<?> getContests(Principal principal, @RequestParam(defaultValue = "true") Boolean joined) {
        JwtAuthenticationToken token = (JwtAuthenticationToken) principal;
        UserDetailsImpl user = userDetailsService.loadUserByToken(token);
        List<Contest> contests = contestService.loadContestsUserCanJoin(user);
        List<ContestDto> contestResponse = contests.stream().map(ContestDto::new).toList();
        return ResponseEntity.ok(contestResponse);
    }

    /***
     * This endpoint lists contest in which the user is participating
     * @param principal The user's JWT token
     */
    @GetMapping("/list/joined")
    public ResponseEntity<?> getUserContestList(Principal principal) {
        JwtAuthenticationToken token = (JwtAuthenticationToken) principal;
        UserDetails user = userDetailsService.loadUserByToken(token);
        List<ContestDto> contests = contestRepository.findByUsersContainsAndIsPublishedTrue(userRepository.findByUsername(user.getUsername()).orElseThrow(null)).stream().map(ContestDto::new).toList();
        return ResponseEntity.ok(contests);
    }

    /***
     * This endpoint lists the leaderboard of a contest
     * @param principal The user's JWT token
     */
    @GetMapping("/{contestId}/leaderboard")
    public ResponseEntity<?> getContestLeaderboard(Principal principal, @PathVariable Integer contestId) {
        JwtAuthenticationToken token = (JwtAuthenticationToken) principal;
        UserDetails user = userDetailsService.loadUserByToken(token);
        // TODO custom body indicating that the user might not be participant of the contest
        // Not assigning to a variable as it's only needed for validation
        contestRepository.findByIdAndUsersContainsAndIsPublishedTrue(contestId, userRepository.findByUsername(user.getUsername()).orElseThrow(null))
                .orElseThrow(() -> new EntityNotFoundException("Contest not found"));
        Object[][] leaderboardMatrix = contestGradingRepository.getLeaderboardByContestId(contestId);
        // Maybe into a function as it's used in BasicController.getLeaderboard() too?
        Set<LeaderboardDto> leaderboard = new LinkedHashSet<>();
        for(Object[] row : leaderboardMatrix) {
            leaderboard.add(new LeaderboardDto((String) row[0], Math.toIntExact((Long) row[1])));
        }
        return ResponseEntity.ok(leaderboard);
    }

    /***
     * This endpoint allows a user to join a contest
     * @param principal The user's JWT token
     * @return Contest object with updated user list
     */
    @PostMapping("/{contestId}/join")
    public ResponseEntity<?> joinContest(Principal principal, @PathVariable Integer contestId) {
        JwtAuthenticationToken token = (JwtAuthenticationToken) principal;
        UserDetailsImpl user = userDetailsService.loadUserByToken(token);
        // TODO custom body indicating that the user might not be participant of the contest
        Contest contest = contestService.loadContestsUserCanJoin(user).stream()
                .filter(contestDto -> contestDto.getId().equals(contestId)).findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Contest not found"));
        // Don't throw as it will never be empty - loadUserByToken() will populate db if needed
        contest.getUsers().add(userRepository.findByUsername(user.getUsername()).orElseThrow(null));
        contestRepository.save(contest);
        return ResponseEntity.ok(new ContestDto(contest));
    }
}
