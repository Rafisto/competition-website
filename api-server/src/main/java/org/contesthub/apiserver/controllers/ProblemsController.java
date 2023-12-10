package org.contesthub.apiserver.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import org.contesthub.apiserver.databaseInterface.DTOs.ContestGradingDto;
import org.contesthub.apiserver.databaseInterface.models.Contest;
import org.contesthub.apiserver.databaseInterface.models.ContestGrading;
import org.contesthub.apiserver.databaseInterface.models.ContestGradingId;
import org.contesthub.apiserver.databaseInterface.models.ContestProblem;
import org.contesthub.apiserver.databaseInterface.repositories.ContestGradingRepository;
import org.contesthub.apiserver.databaseInterface.repositories.ContestProblemRepository;
import org.contesthub.apiserver.databaseInterface.repositories.ContestRepository;
import org.contesthub.apiserver.databaseInterface.repositories.UserRepository;
import org.contesthub.apiserver.services.ContestService;
import org.contesthub.apiserver.services.UserDetailsImpl;
import org.contesthub.apiserver.services.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

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

    @Autowired
    private UserRepository userRepository;

    private Logger logger = LoggerFactory.getLogger(ProblemsController.class);

    @Operation(summary = "Submit solution to a problem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Indicates successful submission", content = {@Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ContestGradingDto.class)
            )}),
            @ApiResponse(responseCode = "404", description = "Indicates unknown problem", content = {@Content()})
    })
    @PostMapping(value="{problemId}/submit", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @Transactional
    @Modifying
    public ResponseEntity<?> submitSolution(Principal principal,
                                            @RequestParam() String answer,
                                            @PathVariable Integer problemId) {
        UserDetailsImpl userDetails = userDetailsService.loadUserByToken((JwtAuthenticationToken) principal);

        // Finds problem with a matching id within the contests the user joined
        ContestProblem matchingProblem = userDetails.getUser().getContests().stream()
                .map(Contest::getContestProblems).reduce(new LinkedHashSet<ContestProblem>(), (reducedSet, newSet) -> {
                    reducedSet.addAll(newSet);
                    return reducedSet;
                }).stream().filter(contestProblem -> contestProblem.getId().equals(problemId)).findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Contest Not found"));

        // Creates a new submission
        logger.info("Creating new submission for user {} for problem {} - {}", userDetails.getUser().getUsername(), matchingProblem.getId(), answer);
        ContestGrading contestGrading = new ContestGrading(userDetails.getUser(), matchingProblem, answer);
        contestGrading.setId(new ContestGradingId(userDetails.getUser().getId(), matchingProblem.getId()));
        contestGradingRepository.saveAndFlush(contestGrading);

        // Refreshes the submission object and returns it
        // TODO: submittedAt: null - JSON
        ContestGradingDto contestGradingResponse = new ContestGradingDto(
                contestGradingRepository.findByUserAndProblem(userDetails.getUser(), matchingProblem));
        return ResponseEntity.ok(contestGradingResponse);
    }

    @GetMapping("{problemId}")
    public ResponseEntity<?> getProblem() {
        return ResponseEntity.ok("Hello World!");
    }

    @PostMapping("{problemId}")
    public ResponseEntity<?> submitProblemSolution() {
        return ResponseEntity.ok("Hello World!");
    }
}
