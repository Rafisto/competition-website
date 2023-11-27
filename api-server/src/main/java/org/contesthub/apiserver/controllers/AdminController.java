package org.contesthub.apiserver.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.contesthub.apiserver.databaseInterface.DTOs.ContestDto;
import org.contesthub.apiserver.databaseInterface.DTOs.ContestProblemDto;
import org.contesthub.apiserver.databaseInterface.models.Contest;
import org.contesthub.apiserver.databaseInterface.models.Group;
import org.contesthub.apiserver.databaseInterface.repositories.ContestProblemRepository;
import org.contesthub.apiserver.databaseInterface.repositories.ContestRepository;
import org.contesthub.apiserver.databaseInterface.repositories.GroupRepository;
import org.contesthub.apiserver.requests.CreateContestProblemRequest;
import org.contesthub.apiserver.requests.CreateContestRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO test all endpoints, didn't have time for it yet
@RestController
@RequestMapping(value = "/admin/",
                consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE,
                            MediaType.APPLICATION_JSON_VALUE})
public class AdminController {
    @Autowired
    ContestRepository contestRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    ContestProblemRepository contestProblemRepository;

    public Set<Group> listGroupIdToGroupReference(@NotNull Integer[] groupIds) {
        Set<Group> groupObjectSet = new LinkedHashSet<>();
        for(Integer group : groupIds) {
            Group groupObject = groupRepository.getReferenceById(group);
            groupObjectSet.add(groupObject);
        }
        return groupObjectSet;
    }

    @PostMapping(value = "contest/create", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> createNewContest(@NotNull @Valid CreateContestRequest request) {
        if (!request.isValid()) {
            final Map<String, Object> body = new HashMap<>();
            body.put("status", HttpServletResponse.SC_BAD_REQUEST);
            body.put("error", "Bad Request");
            body.put("message", "Not a valid contest object");
            body.put("path", "/admin/contest/create");
            ResponseEntity.badRequest().body(body);
        };

        Set<Group> groupObjectSet = listGroupIdToGroupReference(request.getGroups());

        Contest contest = new Contest(request.getName(), request.getDescription(), request.getPublished(), groupObjectSet);
        contestRepository.saveAndFlush(contest);
        ContestDto newContest = new ContestDto(contestRepository.findByTitle(contest.getTitle()));
        return ResponseEntity.ok(newContest);
    }

    @PutMapping("contest/{contestId}/edit")
    public ResponseEntity<?> editContest(@PathVariable Integer contestId, ContestRequest request) {
        Contest contest = contestRepository.findById(contestId).orElseThrow(() ->
                new EntityNotFoundException("Could not find contest with id " + contestId));
        if (request.getGroups() != null){
            Set<Group> groupObjectSet = listGroupIdToGroup(request.getGroups());
            contest.setGroups(groupObjectSet);
        }
        if (request.getTitle() != null){
            contest.setTitle(request.getTitle());
        }
        if (request.getDescription() != null){
            contest.setDescription(request.getDescription());
        }
        if (request.getPublished() != null){
            contest.setIsPublished(request.getPublished());
        }
        contestRepository.saveAndFlush(contest);

        return ResponseEntity.ok(new ContestDto(contest));
    }

    @PostMapping("contest/{contestId}/publish")
    public ResponseEntity<?> publishContest(@PathVariable Integer contestId) {
        contestRepository.updateIsPublishedById(true, contestId);
        ContestDto contest = new ContestDto(contestRepository.findById(contestId).orElseThrow(() ->
                new EntityNotFoundException("Could not find contest with id " + contestId)));
        return ResponseEntity.ok(contest);
    }

    @DeleteMapping(value = "contest/{contestId}", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> deleteContest(@PathVariable Integer contestId){
        ContestDto contest = new ContestDto(contestRepository.findById(contestId).orElseThrow(() ->
                new EntityNotFoundException("Could not find contest with id " + contestId)));
        contestRepository.deleteById(contestId);
        return ResponseEntity.ok(contest);
    }

    @GetMapping(value = "contest/list", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> getContestList(@RequestParam(required = false) Boolean isPublished) {
        if (isPublished == null) {
            return ResponseEntity.ok(contestRepository.getAll().stream().map(ContestDto::new).collect(Collectors.toList()));
        } else if (isPublished == Boolean.TRUE) {
            return ResponseEntity.ok(contestRepository.findByIsPublishedTrue().stream().map(ContestDto::new).collect(Collectors.toList()));
        } else if (isPublished == Boolean.FALSE) {
            return ResponseEntity.ok(contestRepository.findByIsPublishedFalse().stream().map(ContestDto::new).collect(Collectors.toList()));
        }
        List<ContestDto> contestList = contestRepository.getAll().stream().map(ContestDto::new).collect(Collectors.toList());
        return ResponseEntity.ok(contestList);
    }

    @GetMapping(value = {"contest/{contestId}/problems"}, consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> getContestProblemList(@PathVariable Integer contestId) {
        List<ContestProblemDto> contestProblems = contestProblemRepository.findAllByContestId(contestId).stream().map(ContestProblemDto::new).toList();
        if (contestProblems.isEmpty()) {
            // Maybe check for contest existence first?
            // It will slow down the request though
            return ResponseEntity.badRequest().body("No problems found for contest with id " + contestId);
        }
        return ResponseEntity.ok(contestProblems);
    }

//    @PutMapping(value = "contest/{contestId}/problems/add", consumes = MediaType.ALL_VALUE)
//    public ResponseEntity<?> addContestProblem(@PathVariable String contestId, @Valid @RequestBody CreateContestProblemRequest request) {
//        return ResponseEntity.ok().build();
//    }
}
