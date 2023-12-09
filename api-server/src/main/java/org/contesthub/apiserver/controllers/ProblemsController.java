package org.contesthub.apiserver.controllers;

import jakarta.persistence.EntityNotFoundException;
import org.contesthub.apiserver.databaseInterface.DTOs.ContestGradingDto;
import org.contesthub.apiserver.databaseInterface.models.Contest;
import org.contesthub.apiserver.databaseInterface.models.ContestGrading;
import org.contesthub.apiserver.databaseInterface.models.ContestProblem;
import org.contesthub.apiserver.databaseInterface.repositories.ContestGradingRepository;
import org.contesthub.apiserver.databaseInterface.repositories.ContestProblemRepository;
import org.contesthub.apiserver.databaseInterface.repositories.ContestRepository;
import org.contesthub.apiserver.services.ContestService;
import org.contesthub.apiserver.services.UserDetailsImpl;
import org.contesthub.apiserver.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/problems/")
public class ProblemsController {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ContestRepository contestRepository;

    @Autowired
    private ContestService contestService;

    @Autowired
    private ContestGradingRepository contestGradingRepository;

    @Autowired
    private ContestProblemRepository contestProblemRepository;

    @PostMapping(value="/problems/{problemId}/submit", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> submitSolution(Principal principal,
                                            @RequestParam() String answer,
                                            @PathVariable Integer problemId) {
        UserDetailsImpl userDetails = userDetailsService.loadUserByToken((JwtAuthenticationToken) principal);
        List<Contest> contests = contestService.loadUserContests(userDetails).stream()
                .filter(contest -> contest.getContestProblems().stream().filter(problem -> Objects.equals(problem.getId(), problemId)).count() == 1)
                .toList();
        ContestProblem matchingProblem = contestService.loadUserContests(userDetails).stream()
                .map(Contest::getContestProblems).reduce(new LinkedHashSet<ContestProblem>(), (reducedSet, newSet) -> {
                    reducedSet.addAll(newSet);
                    return reducedSet;
                }).stream()
                .filter(contestProblem -> contestProblem.getId().equals(problemId)).findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Contest Not found"));
        ContestGrading contestGrading = new ContestGrading();
        contestGrading.setContest(contests.get(0));
        return ResponseEntity.ok().build();
    }
}
