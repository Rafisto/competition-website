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
import org.contesthub.apiserver.databaseInterface.DTOs.GroupDto;
import org.contesthub.apiserver.databaseInterface.DTOs.UserDto;
import org.contesthub.apiserver.databaseInterface.models.Contest;
import org.contesthub.apiserver.databaseInterface.models.ContestProblem;
import org.contesthub.apiserver.databaseInterface.models.Group;
import org.contesthub.apiserver.databaseInterface.models.User;
import org.contesthub.apiserver.databaseInterface.repositories.ContestProblemRepository;
import org.contesthub.apiserver.databaseInterface.repositories.ContestRepository;
import org.contesthub.apiserver.databaseInterface.repositories.GroupRepository;
import org.contesthub.apiserver.databaseInterface.repositories.UserRepository;
import org.contesthub.apiserver.models.requests.ContestRequest;
import org.contesthub.apiserver.models.requests.ContestProblemRequest;
import org.contesthub.apiserver.models.requests.GroupRequest;
import org.contesthub.apiserver.services.ContestService;
import org.contesthub.apiserver.services.GroupService;
import org.contesthub.apiserver.services.UserDetailsImpl;
import org.contesthub.apiserver.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.io.InvalidObjectException;
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

    @Autowired
    GroupService groupService;

    @Autowired
    UserDetailsServiceImpl userService;

    @Autowired
    ContestService contestService;

    @Autowired
    UserRepository userRepository;

    /***
     * This endpoint lets the admin create a new contest
     * @param request Matches the ContestRequest object representing the contest to be created. This will be fully validated.
     * @return The newly created contest object
     */
    @Operation(summary = "Create a new contest")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description= " The newly created contest object", content =
                    {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ContestDto.class))
                    }),
            @ApiResponse(responseCode = "400", description = "Indicates invalid contest object", content = {@Content()}),
            // This is raised within the GroupService
            @ApiResponse(responseCode = "404", description = "Tried to provide nonexistent group(s)")
    })
    @PostMapping(value = "contest/create", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> createNewContest(@Parameter(description = "Matches the ContestRequest object representing the contest to be created. This will be fully validated.", required = true) @Valid ContestRequest request) {
        if (!request.isValid()) {
            throw new IllegalArgumentException("Not a valid contest object");
        };

        Set<Group> groupObjectSet = groupService.loadGroupsFromIdList(request.getGroups());

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
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "The edited contest object", content =
                {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = ContestDto.class))
                }),
        @ApiResponse(responseCode = "404", description = "Contest or Group(s) not found. Specified in response"),
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
            Set<Group> groupObjectSet = groupService.loadGroupsFromIdList(request.getGroups());
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
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "The edited contest object", content =
                {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = ContestDto.class))
                }),
        @ApiResponse(responseCode = "404", description = "Contest not found")
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
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "The deleted contest object", content =
                {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = ContestDto.class))
                }),
        @ApiResponse(responseCode = "404", description = "Contest not found")
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
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of contests matching the query", content =
                {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = ContestDto.class))
                }),
        @ApiResponse(responseCode = "400", description = "Invalid value of isPublished param")
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
        } else {
            throw new IllegalArgumentException("Invalid value for isPublished");
        }
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
            @ApiResponse(responseCode="400", description="Indicates invalid contest problem object", content ={@Content()}),
            @ApiResponse(responseCode="404", description="Indicates that the contest does not exist", content ={@Content()})
    })
    @PostMapping(value = "contest/{contestId}/problems/create")
    public ResponseEntity<?> addContestProblem(@Parameter(description = "Id of the contest in which problem will be added") @PathVariable Integer contestId,
                                               @Parameter(description = "Represents problem statement that will be created") @Valid ContestProblemRequest request) {
        Contest contest = contestRepository.findById(contestId).orElseThrow(() ->
                new EntityNotFoundException("Could not find contest with id " + contestId));
        if (!request.isValid()) {
            throw new IllegalArgumentException("Not a valid contest problem object");
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
            @ApiResponse(responseCode="404", description="Indicates that the problem statement does not exist", content ={@Content()})
    })
    @GetMapping(value = "problems/{problemId}", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> getProblemDetails(@Parameter(description = "Id of the problem statement to be fetched") @PathVariable Integer problemId) {
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
            @ApiResponse(responseCode="404", description="Indicates that the problem statement does not exist", content ={@Content()})
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
                throw new IllegalArgumentException("Autograding answer cannot be null or empty");
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
                throw new IllegalArgumentException("Deadline cannot be in the past");
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
    @ApiResponses(value = {
            @ApiResponse(responseCode="200", description="The deleted contest problem object", content =
                    {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ContestProblemDto.class))
                    }),
            @ApiResponse(responseCode="404", description="Indicates that the problem statement does not exist", content ={@Content()})
    })
    @DeleteMapping(value = "problems/{problemId}", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> deleteProblem(@Parameter(description = "Id of a problem statement to be deleted") @PathVariable Integer problemId) {
        ContestProblemDto contestProblem = new ContestProblemDto(contestProblemRepository.findById(problemId).orElseThrow(() ->
                new EntityNotFoundException("Could not find contest problem with id " + problemId)));
        contestProblemRepository.deleteById(problemId);
        return ResponseEntity.ok(contestProblem);
    }

    @Operation(summary="Get all groups in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode="200", description="List of groups in the database", content =
                    {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = GroupDto.class))
                    }),
            @ApiResponse(responseCode="404", description="Indicates that no groups exist in the database", content ={@Content()})
    })
    @GetMapping(value = "groups/list", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> getGroupList() {
        // Is there a point in returning all info though? It's admin endpoint so I guess?
        List<GroupDto> groups = groupRepository.findAll().stream().map(GroupDto::new).toList();
        if (groups.isEmpty()){
            throw new EntityNotFoundException("No groups found in the database");
        }
        return ResponseEntity.ok(groups);
    }

    @Operation(summary="Get details of a group")
    @ApiResponses({
            @ApiResponse(responseCode="200", description="The requested group object", content =
                    {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = GroupDto.class))
                    }),
            @ApiResponse(responseCode="404", description="Indicates that the group does not exist", content ={@Content()})

    })
    @GetMapping(value = "groups/{groupId}", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> getGroupDetails(@Parameter(description = "Id of a group to be fetched") @PathVariable Integer groupId) {
        GroupDto group = new GroupDto(groupRepository.findById(groupId).orElseThrow(() ->
                new EntityNotFoundException("Could not find group with id " + groupId)));
        return ResponseEntity.ok(group);
    }

    @Operation(summary="Create a new group")
    @ApiResponses(value = {
            @ApiResponse(responseCode="200", description="The newly created group object", content =
                    {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = GroupDto.class))
                    }),
            @ApiResponse(responseCode="400", description="Indicates invalid group object", content ={@Content()})
    })
    @PostMapping(value = "groups/create", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> createGroup(@Parameter(description = "Matches the GroupRequest object representing the group to be created. This will be fully validated.", required = true) GroupRequest request) {
        Group group = new Group();
        group.setName(request.getName());
        group.setContests(contestService.loadContestsFromIdList(request.getContests()));
        group.setUsers(userService.loadUsersFromIdList(request.getUsers()));
        Group newGroup = groupRepository.saveAndFlush(group);
        return ResponseEntity.ok(newGroup);
    }

    @Operation(summary="Get all users in a group")
    @ApiResponses(value = {
            @ApiResponse(responseCode="200", description="List of users in the group", content =
                    {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
                    }),
            @ApiResponse(responseCode="404", description="Indicates that the group does not exist", content ={@Content()})
    })
    @GetMapping(value = "groups/{groupId}/users", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> getGroupUsers(@Parameter(description = "Id of a fetched group") @PathVariable Integer groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() ->
                new EntityNotFoundException("Could not find group with id " + groupId));
        return ResponseEntity.ok(group.getUsers().stream().map(UserDto::new).toList());
    }

    @Operation(summary="Add a user to a group", description = "To add multiple users use the group edit endpoint")
    @ApiResponses(value = {
            @ApiResponse(responseCode="200", description="The edited group object", content =
                    {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
                    }),
            @ApiResponse(responseCode="404", description="Indicates that the group or user does not exist", content ={@Content()})
    })
    @PostMapping(value = "groups/{groupId}/users/add", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> addUserToGroup(@Parameter(description = "Id of a group to which user will be added") @PathVariable Integer groupId,
                                            @Parameter(description = "Username of the user that will be added to group") @RequestParam String username) {
        Group group = groupRepository.findById(groupId).orElseThrow(() ->
                new EntityNotFoundException("Could not find group with id " + groupId));

        User userObject;
        try {
            UserDetailsImpl user = userService.loadUserByUsername(username);
            userObject = user.getUser();
        } catch(UsernameNotFoundException e) {
            userObject = userService.createTemporaryUser(username);
        }

        group.getUsers().add(userObject);
        groupRepository.saveAndFlush(group);
        return ResponseEntity.ok(group.getUsers().stream().map(UserDto::new).toList());
    }

    @Operation(summary="Remove a user from a group", description = "To add multiple users use the group edit endpoint")
    @ApiResponses(value = {
            @ApiResponse(responseCode="200", description="The edited group object", content =
                    {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))
                    }),
            @ApiResponse(responseCode="404", description="Indicates that the group or user does not exist", content ={@Content()})
    })
    @PostMapping(value = "groups/{groupId}/users/remove", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> removeUserFromGroup(@Parameter(description = "Id of a group to which user will be removed") @PathVariable Integer groupId,
                                                 @Parameter(description = "Username of the user that will be removed from the group") @RequestParam String username) {
        Group group = groupRepository.findById(groupId).orElseThrow(() ->
                new EntityNotFoundException("Could not find group with id " + groupId));

        UserDetailsImpl user = userService.loadUserByUsername(username);
        Boolean response = group.getUsers().remove(user.getUser());
        if (!response.equals(Boolean.TRUE)) {
            throw new EntityNotFoundException("Could not find user with username " + username + " in group with id " + groupId);
        } else {
            groupRepository.saveAndFlush(group);
            return ResponseEntity.ok(group.getUsers().stream().map(UserDto::new).toList());
        }
    }

    @Operation(summary="Delete a group. This removes the group permanently from the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode="200", description="The deleted group object", content =
                    {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = GroupDto.class))
                    }),
            @ApiResponse(responseCode="404", description="Indicates that the group does not exist", content ={@Content()})
    })
    @DeleteMapping(value = "groups/{groupId}", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<?> deleteGroup(@Parameter(description = "Id of the group to be deleted") @PathVariable Integer groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() ->
                new EntityNotFoundException("Could not find group with id " + groupId));
        groupRepository.deleteById(groupId);
        return ResponseEntity.ok(new GroupDto(group));
    }

    @Operation(summary="Edit an existing group")
    @ApiResponses(value = {
            @ApiResponse(responseCode="200", description="The edited group object", content =
                    {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = GroupDto.class))
                    }),
            @ApiResponse(responseCode="400", description="Indicates invalid group object", content ={@Content()}),
            @ApiResponse(responseCode="404", description="Indicates that the group does not exist", content ={@Content()})
    })
    @PutMapping(value = "groups/{groupId}/edit", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> editGroup(@Parameter(description = "Id of the group to be modified") @PathVariable Integer groupId,
                                       @Parameter(description = "Modified group. Specify only the fields you want to modify.") GroupRequest request){
        Group group = groupRepository.findById(groupId).orElseThrow(() ->
                new EntityNotFoundException("Could not find group with id " + groupId));
        if (request.getName() != null) {
            if (request.getName().isBlank() && request.getName().length() < 3) {
                throw new IllegalArgumentException("Group name has to be longer than 3 characters");
            }
            group.setName(request.getName());
        }
        if (request.getContests() != null) {
            group.setContests(contestService.loadContestsFromIdList(request.getContests()));
        }
        if (request.getUsers() != null) {
            group.setUsers(userService.loadUsersFromIdList(request.getUsers()));
        }
        groupRepository.saveAndFlush(group);
        return ResponseEntity.ok(new GroupDto(group));
    }
}
