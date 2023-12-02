package org.contesthub.apiserver.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.contesthub.apiserver.databaseInterface.DTOs.ContestDto;
import org.contesthub.apiserver.databaseInterface.DTOs.ContestProblemDto;
import org.contesthub.apiserver.databaseInterface.models.Contest;
import org.contesthub.apiserver.databaseInterface.models.ContestProblem;
import org.contesthub.apiserver.databaseInterface.models.Group;
import org.contesthub.apiserver.databaseInterface.repositories.ContestProblemRepository;
import org.contesthub.apiserver.databaseInterface.repositories.ContestRepository;
import org.contesthub.apiserver.databaseInterface.repositories.GroupRepository;
import org.contesthub.apiserver.models.requests.ContestRequest;
import org.contesthub.apiserver.models.requests.ContestProblemRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

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

    public Set<Group> listGroupIdToGroup(Integer[] groupIds) {
        Set<Group> groupObjectSet = new LinkedHashSet<>();
        for(Integer group : groupIds) {
            Group groupObject = groupRepository.findById(group).orElseThrow(() ->
                    new EntityNotFoundException("Could not find group with id " + group));
            groupObjectSet.add(groupObject);
        }
        return groupObjectSet;
    }

    @PostMapping(value = "contest/create", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> createNewContest(@Valid ContestRequest request) {
        if (!request.isValid()) {
            final Map<String, Object> body = new HashMap<>();
            body.put("status", HttpServletResponse.SC_BAD_REQUEST);
            body.put("error", "Bad Request");
            body.put("message", "Not a valid contest object");
            body.put("path", "/admin/contest/create");
            ResponseEntity.badRequest().body(body);
        };

        Set<Group> groupObjectSet = listGroupIdToGroup(request.getGroups());

        Contest contest = new Contest(request.getTitle(), request.getDescription(), request.getPublished(), groupObjectSet);
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
    public ResponseEntity<?> publishContest(@PathVariable Integer contestId, @RequestParam(defaultValue = "true") Boolean publish) {
        contestRepository.updateIsPublishedById(publish, contestId);
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

    @PostMapping(value = "contest/{contestId}/problems/create", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> addContestProblem(@PathVariable Integer contestId, @Valid ContestProblemRequest request) {
        Contest contest = contestRepository.findById(contestId).orElseThrow(() ->
                new EntityNotFoundException("Could not find contest with id " + contestId));
        if (!request.isValid()) {
            final Map<String, Object> body = new HashMap<>();
            body.put("status", HttpServletResponse.SC_BAD_REQUEST);
            body.put("error", "Bad Request");
            body.put("message", "Not a valid contest problem object");
            body.put("path", "/admin/contest/" + contestId + "/problems/add");
            ResponseEntity.badRequest().body(body);
        };
        ContestProblem contestProblem = new ContestProblem(request.getTitle(), request.getContents(), request.getUseAutograding(), request.getUseAutogradingAnswer(), request.getDeadline(), contest);
        contestProblemRepository.saveAndFlush(contestProblem);
        return ResponseEntity.ok(contestProblem);
    }

    @GetMapping(value = "problems/{problemId}", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> getProblemDetails(@PathVariable Integer problemId) {
        ContestProblemDto contestProblem = new ContestProblemDto(contestProblemRepository.findById(problemId).orElseThrow(() ->
                new EntityNotFoundException("Could not find contest problem with id " + problemId)));
        return ResponseEntity.ok(contestProblem);
    }

    @PutMapping(value = "problems/{problemId}/edit")
    public ResponseEntity<?> editContestProblem(@PathVariable Integer problemId, ContestProblemRequest request){
        ContestProblem contestProblem = contestProblemRepository.findById(problemId).orElseThrow(() ->
                new EntityNotFoundException("Could not find contest problem with id " + problemId));
        if (request.getTitle() != null) {
            contestProblem.setTitle(request.getTitle());
        }
        if (request.getContents() != null) {
            contestProblem.setContents(request.getContents());
        }
        if (request.getUseAutograding() == Boolean.TRUE){
            if (request.getUseAutogradingAnswer() == null) {
                final Map<String, Object> body = new HashMap<>();
                body.put("status", HttpServletResponse.SC_BAD_REQUEST);
                body.put("error", "Bad Request");
                body.put("message", "Autograding answer cannot be null or empty");
                body.put("path", "/admin/problem/" + problemId + "/edit");

                return ResponseEntity.badRequest().body(body);
            } else {
                contestProblem.setUseAutograding(request.getUseAutograding());
                contestProblem.setUseAutogradingAnswer(request.getUseAutogradingAnswer());
            }
        } else if (request.getUseAutograding() == Boolean.FALSE) {
            contestProblem.setUseAutograding(request.getUseAutograding());
            // Possibly remove the expected answer too?
            //contestProblemRepository.updateUseAutogradingAnswerById(null, problemId);
        }
        if (request.getDeadline() != null) {
            if (!request.checkDeadline()) {
                final Map<String, Object> body = new HashMap<>();
                body.put("status", HttpServletResponse.SC_BAD_REQUEST);
                body.put("error", "Bad Request");
                body.put("message", "Deadline cannot be in the past");
                body.put("path", "/admin/problem/" + problemId + "/edit");

                return ResponseEntity.badRequest().body(body);
            }
            contestProblem.setDeadline(request.getDeadline());
        }
        contestProblemRepository.saveAndFlush(contestProblem);

        return ResponseEntity.ok(contestProblem);
    }

    @DeleteMapping(value = "problems/{problemId}", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> deleteProblem(@PathVariable Integer problemId) {
        ContestProblemDto contestProblem = new ContestProblemDto(contestProblemRepository.findById(problemId).orElseThrow(() ->
                new EntityNotFoundException("Could not find contest problem with id " + problemId)));
        contestProblemRepository.deleteById(problemId);
        return ResponseEntity.ok(contestProblem);
    }
}
