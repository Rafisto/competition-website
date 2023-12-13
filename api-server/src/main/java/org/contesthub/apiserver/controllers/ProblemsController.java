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
import org.contesthub.apiserver.services.ContestProblemService;
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
import java.time.Instant;
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
    private ContestProblemService contestProblemService;

    @Autowired
    private UserRepository userRepository;

    private final Logger logger = LoggerFactory.getLogger(ProblemsController.class);

    @Operation(summary = "Submit solution to a problem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Indicates successful submission", content = {@Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ContestGradingDto.class)
            )}),
            @ApiResponse(responseCode = "404", description = "Indicates unknown problem", content = {@Content()})
    })
    @PostMapping(value="{problemId}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @Transactional
    @Modifying
    public ResponseEntity<?> submitSolution(Principal principal,
                                            @RequestParam() String answer,
                                            @RequestParam(required = false) Boolean isFile,
                                            @PathVariable Integer problemId) {
        UserDetailsImpl userDetails = userDetailsService.loadUserByToken((JwtAuthenticationToken) principal);
        ContestProblem matchingProblem = contestProblemService.findMatchingProblem(userDetails.getUser(), problemId);

        if (isFile == null) {
            isFile = Boolean.FALSE;
        }

        // Creates a new submission
        logger.info("Creating new submission for user {} for problem {} - {}", userDetails.getUser().getUsername(), matchingProblem.getId(), answer);
        ContestGrading contestGrading = new ContestGrading(userDetails.getUser(), matchingProblem, answer, isFile);
        contestGrading.setId(new ContestGradingId(userDetails.getUser().getId(), matchingProblem.getId()));
        contestGradingRepository.saveAndFlush(contestGrading);

        // TODO: auto-grading
        ContestGradingDto contestGradingResponse = new ContestGradingDto(
                contestGradingRepository.findByUserAndProblem(userDetails.getUser(), matchingProblem));
        contestGradingResponse.resolveFile(userDetails, contestGradingResponse.getProblem());
        return ResponseEntity.ok(contestGradingResponse);
    }

    @Operation(summary = "Update solution to a problem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Indicates successful submission", content = {@Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ContestGradingDto.class)
            )}),
            @ApiResponse(responseCode = "404", description = "Indicates unknown problem", content = {@Content()})
    })
    @PutMapping(value="{problemId}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @Transactional
    @Modifying
    public ResponseEntity<?> updateSolution(Principal principal,
                                            @RequestParam() String answer,
                                            @RequestParam(required = false) Boolean isFile,
                                            @PathVariable Integer problemId) {
        UserDetailsImpl userDetails = userDetailsService.loadUserByToken((JwtAuthenticationToken) principal);
        ContestProblem matchingProblem = contestProblemService.findMatchingProblem(userDetails.getUser(), problemId);

        if (isFile == null) {
            isFile = Boolean.FALSE;
        }

        // Creates a new submission
        logger.info("Updating submission for user {} for problem {} - {}", userDetails.getUser().getUsername(), matchingProblem.getId(), answer);
        ContestGrading contestGrading = new ContestGrading(userDetails.getUser(), matchingProblem, answer, isFile);
        contestGrading.setId(new ContestGradingId(userDetails.getUser().getId(), matchingProblem.getId()));
        contestGrading.setLastUpdatedAt(Instant.now());
        contestGradingRepository.saveAndFlush(contestGrading);

        // TODO: auto-grading
        ContestGradingDto contestGradingResponse = new ContestGradingDto(
                contestGradingRepository.findByUserAndProblem(userDetails.getUser(), matchingProblem));
        contestGradingResponse.resolveFile(userDetails, contestGradingResponse.getProblem());
        return ResponseEntity.ok(contestGradingResponse);
    }

    @GetMapping(value="{problemId}")
    @Transactional
    public ResponseEntity<?> getProblem(Principal principal, @PathVariable Integer problemId) {
        UserDetailsImpl userDetails = userDetailsService.loadUserByToken((JwtAuthenticationToken) principal);
        ContestProblem matchingProblem = contestProblemService.findMatchingProblem(userDetails.getUser(), problemId);
        ContestGradingDto contestGradingResponse = new ContestGradingDto(contestGradingRepository.findByUserAndProblem(userDetails.getUser(), matchingProblem));
        contestGradingResponse.resolveFile(userDetails, matchingProblem.getId());
        return ResponseEntity.ok(contestGradingResponse);
    }
}
