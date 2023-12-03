package org.contesthub.apiserver.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    // TODO: Possibly move this to a service
    public Set<Group> listGroupIdToGroup(Integer[] groupIds) {
        Set<Group> groupObjectSet = new LinkedHashSet<>();
        for(Integer group : groupIds) {
            Group groupObject = groupRepository.findById(group).orElseThrow(() ->
                    new EntityNotFoundException("Could not find group with id " + group));
            groupObjectSet.add(groupObject);
        }
        return groupObjectSet;
    }

    /***
     * This endpoint lets the admin create a new contest
     * @param request Matches the ContestRequest object representing the contest to be created. This will be fully validated.
     * @return The newly created contest object
     */
    @Operation(summary="Create a new contest")
    @ApiResponses(value = {
            @ApiResponse(responseCode="200", description="The newly created contest object", content =
                    {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ContestDto.class))
                    }),
            @ApiResponse(responseCode="400", description="Indicates invalid contest object", content ={@Content()})
    })
    @PostMapping(value = "contest/create", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> createNewContest(@Parameter(description = "Matches the ContestRequest object representing the contest to be created. This will be fully validated.", required = true) @Valid ContestRequest request) {
        if (!request.isValid()) {
            final Map<String, Object> body = new HashMap<>();
            body.put("status", HttpServletResponse.SC_BAD_REQUEST);
            body.put("error", "Bad Request");
            body.put("message", "Not a valid contest object");
            body.put("path", "/admin/contest/create");
            return ResponseEntity.badRequest().body(body);
        };

        Set<Group> groupObjectSet = listGroupIdToGroup(request.getGroups());

        Contest contest = new Contest(request.getTitle(), request.getDescription(), request.getPublished(), groupObjectSet);
        contestRepository.saveAndFlush(contest);
        ContestDto newContest = new ContestDto(contestRepository.findByTitle(contest.getTitle()));
        return ResponseEntity.ok(newContest);
    }

    /***
     * This endpoint lets the admin edit an already existing contest.
     * @param contestId Id of the contest to be edited. Must be already existing.
     * @param request Matches the ContestRequest object representing the contest to be edited. At least one of the fields must be provided.
     *                Can contain any of the fields in the ContestRequest object, none of them is required.
     *                This means that it won't be fully validated, each field will be validated individually instead.
     * @return The edited contest object
     */
    @Operation(summary="Edit an existing contest")
    @ApiResponse(responseCode="200", description="The edited contest object", content =
            {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ContestDto.class))
            })
    @PutMapping("contest/{contestId}/edit")
    public ResponseEntity<?> editContest(@Parameter(description = "Id of the contest to be edited") @PathVariable Integer contestId,
                                         @Parameter(description = """
                                                     Matches the ContestRequest object representing the contest to be edited. At least one of the fields must be provided.
                                                     Can contain any of the fields in the ContestRequest object, none of them is required.
                                                     This means that it won't be fully validated, each field will be validated individually instead.
                                                     """) ContestRequest request) {
        // TODO: custom body indicating that the contest might not exist
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

    /***
     * This endpoint is a shorthand toggle for publishing/unpublishing a contest
     * @param contestId Id of the contest to be published/unpublished
     * @param publish Set to false if the contest is to be unpublished, in other cases this can be omitted.
     * @return The edited contest object
     */
    @Operation(summary="Publish/unpublish a contest")
    @ApiResponse(responseCode="200", description="The edited contest object", content =
            {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ContestDto.class))
            })
    @PostMapping("contest/{contestId}/publish")
    public ResponseEntity<?> publishContest(@Parameter(description = "Id of the contest to be updated") @PathVariable Integer contestId,
                                            @Parameter(description = "Set to false to make contest private") @RequestParam(defaultValue = "true") Boolean publish) {
        contestRepository.updateIsPublishedById(publish, contestId);
        // TODO: custom body indicating that the contest might not exist
        ContestDto contest = new ContestDto(contestRepository.findById(contestId).orElseThrow(() ->
                new EntityNotFoundException("Could not find contest with id " + contestId)));
        return ResponseEntity.ok(contest);
    }

    /***
     * This endpoint lets the admin delete a contest. This removes the contest permanently from the database.
     * @param contestId Id of the contest to be deleted. Must be already existing.
     * @return The deleted contest object
     */
    @Operation(summary="Delete a contest. This removes the contest permanently from the database.")
    @ApiResponse(responseCode="200", description="The deleted contest object", content =
            {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ContestDto.class))
            })
    @DeleteMapping(value = "contest/{contestId}", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> deleteContest(@Parameter(description = "Id of the contest to be deleted")
                                           @PathVariable Integer contestId){
        // TODO: custom body indicating that the contest might not exist
        ContestDto contest = new ContestDto(contestRepository.findById(contestId).orElseThrow(() ->
                new EntityNotFoundException("Could not find contest with id " + contestId)));
        contestRepository.deleteById(contestId);
        return ResponseEntity.ok(contest);
    }

    /***
     * This endpoint lets the admin list all contests in the database.
     * @param isPublished Filter by published/unpublished contests. Can be true or false.
     *                    If not provided, all contests are returned.
     * @return List of contests matching the query
     */
    @Operation(summary="List all contests in the database")
    @ApiResponse(responseCode="200", description="List of contests matching the query", content =
            {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ContestDto.class))
            })
    @GetMapping(value = "contest/list", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> getContestList(@Parameter(description = "Filter by published/unpublished contents. If not provided all contests are returned")
                                            @RequestParam(required = false) Boolean isPublished) {
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

    /***
     * This endpoint lets the admin list all problems in a contest.
     * @param contestId Id of the contest whose problems are to be listed. Must be already existing.
     * @return List of problems in the contest
     */
    @Operation(summary="List all problem statements in a contest")
    @ApiResponses(value = {
            @ApiResponse(responseCode="200", description="List of problems in the contest", content =
                    {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ContestProblemDto.class))
                    }),
            @ApiResponse(responseCode="400", description="Indicates that no problems exist within given contest", content ={@Content()})
    })
    @GetMapping(value = {"contest/{contestId}/problems"}, consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> getContestProblemList(@Parameter(description = "Id of the contest whose problems are to be listed") @PathVariable Integer contestId) {
        List<ContestProblemDto> contestProblems = contestProblemRepository.findAllByContestId(contestId).stream().map(ContestProblemDto::new).toList();
        if (contestProblems.isEmpty()) {
            // Maybe check for contest existence first?
            // It will slow down the request though
            return ResponseEntity.badRequest().body("No problems found for contest with id " + contestId);
        }
        return ResponseEntity.ok(contestProblems);
    }

    /***
     * This endpoint lets the admin create a new problem statement.
     * @param contestId Id of the contest to in which problem statement will be created. Must be already existing.
     * @param request Matches the ContestProblemRequest object representing the contest problem to be created.
     * @return The edited contest object
     */
    @Operation(summary="Add a problem statement to a contest")
    @ApiResponses(value = {
            @ApiResponse(responseCode="200", description="The newly created contest problem object", content =
                    {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ContestProblemDto.class))
                    }),
            @ApiResponse(responseCode="400", description="Indicates invalid contest problem object", content ={@Content()})
    })
    @PostMapping(value = "contest/{contestId}/problems/create")
    public ResponseEntity<?> addContestProblem(@Parameter(description = "Id of the contest in which problem will be added") @PathVariable Integer contestId,
                                               @Parameter(description = "Represents problem statement that will be created") @Valid ContestProblemRequest request) {
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

    /***
     * This endpoint lets the admin get details of a problem statement.
     * @param problemId Id of the problem statement to be fetched. Must be already existing.
     * @return Requested problem statement object
     */
    @Operation(summary="Get details of a problem statement")
    @ApiResponses(value = {
            @ApiResponse(responseCode="200", description="The requested contest problem object", content =
                    {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ContestProblemDto.class))
                    }),
//            @ApiResponse(responseCode="400", description="Indicates that the problem statement does not exist", content ={@Content()})
    })
    @GetMapping(value = "problems/{problemId}", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> getProblemDetails(@Parameter(description = "Id of the problem statement to be fetched") @PathVariable Integer problemId) {
        // TODO: custom body indicating that the problem statement might not exist
        ContestProblemDto contestProblem = new ContestProblemDto(contestProblemRepository.findById(problemId).orElseThrow(() ->
                new EntityNotFoundException("Could not find contest problem with id " + problemId)));
        return ResponseEntity.ok(contestProblem);
    }

    /***
     * This endpoint lets the admin edit an already existing problem statement.
     * @param problemId Id of the problem to be edited. Must be already existing.
     * @param request Matches the ContestProblemRequest object representing the contest to be edited. At least one of the fields must be provided.
     *                Can contain any of the fields in the ContestProblemRequest object, none of them is required.
     *                This means that it won't be fully validated, each field will be validated individually instead.
     * @return The edited contest object
     */
    @Operation(summary="Edit an existing problem statement")
    @ApiResponses(value = {
            @ApiResponse(responseCode="200", description="The edited contest problem object", content =
                    {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ContestProblemDto.class))
                    }),
            @ApiResponse(responseCode="400", description="Indicates invalid contest problem object", content ={@Content()}),
//            @ApiResponse(responseCode="400", description="Indicates that the problem statement does not exist", content ={@Content()})
    })
    @PutMapping(value = "problems/{problemId}/edit")
    public ResponseEntity<?> editContestProblem(@Parameter(description = "Id of a problem statement to be edited") @PathVariable Integer problemId,
                                                @Parameter(description = """
                                                            Matches the ContestProblemRequest object representing the contest to be edited. At least one of the fields must be provided.
                                                            Can contain any of the fields in the ContestProblemRequest object, none of them is required.
                                                            This means that it won't be fully validated, each field will be validated individually instead.
                                                            """) ContestProblemRequest request){
        // TODO: custom body indicating that the problem statement might not exist
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

    /***
     * This endpoint lets the admin delete a problem statement. This removes the problem statement permanently from the database.
     * @param problemId Id of the problem statement to be deleted. Must be already existing.
     * @return The deleted problem statement object
     */
    @Operation(summary="Delete a problem statement. This removes the problem statement permanently from the database.")
    @ApiResponse(responseCode="200", description="The deleted contest problem object", content =
            {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ContestProblemDto.class))
            })
    @DeleteMapping(value = "problems/{problemId}", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> deleteProblem(@Parameter(description = "Id of a problem statement to be deleted") @PathVariable Integer problemId) {
        ContestProblemDto contestProblem = new ContestProblemDto(contestProblemRepository.findById(problemId).orElseThrow(() ->
                new EntityNotFoundException("Could not find contest problem with id " + problemId)));
        contestProblemRepository.deleteById(problemId);
        return ResponseEntity.ok(contestProblem);
    }
}
