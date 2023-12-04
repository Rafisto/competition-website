package org.contesthub.apiserver.databaseInterface.DTOs;

import jakarta.validation.constraints.Size;
import org.contesthub.apiserver.databaseInterface.models.Contest;
import org.contesthub.apiserver.databaseInterface.models.ContestProblem;
import org.contesthub.apiserver.databaseInterface.models.Group;
import org.contesthub.apiserver.databaseInterface.models.User;

import java.io.Serializable;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * DTO for {@link Contest}
 */
public class ContestDto implements Serializable {
    private Integer id;
    @Size(max = 255)
    private String title;
    private String description;
    private Boolean isPublished;
    private Set<GroupDto> groups;
    private Set<ContestProblemDto> contestProblems;
    private Set<UserDto> users;

    public ContestDto() {
    }

    static class GroupDto {
        public Integer id;
        public String name;

        public GroupDto(Integer id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    static class ContestProblemDto {
        public Integer id;
        public String title;
        public String contents;
        public Boolean useAutograding;
        public String useAutogradingAnswer;
        public Instant deadline;

        public ContestProblemDto(Integer id, String title, String contents, Boolean useAutograding, String useAutogradingAnswer, Instant deadline) {
            this.id = id;
            this.title = title;
            this.contents = contents;
            this.useAutograding = useAutograding;
            this.useAutogradingAnswer = useAutogradingAnswer;
            this.deadline = deadline;
        }
    }

    static class UserDto {
        public Integer id;
        public String username;
        public String email;

        public UserDto(Integer id, String username, String email) {
            this.id = id;
            this.username = username;
            this.email = email;
        }
    }

    public ContestDto(Integer id, String title, String description, Boolean isPublished, Set<GroupDto> groups, Set<ContestProblemDto> contestProblems, Set<UserDto> users) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.isPublished = isPublished;
        this.groups = groups;
        this.contestProblems = contestProblems;
        this.users = users;
    }

    public ContestDto(Integer id, String title, String description, Boolean isPublished) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.isPublished = isPublished;
    }

    public ContestDto(Contest contest) {
        this(contest.getId(), contest.getTitle(), contest.getDescription(), contest.getIsPublished(),
                new LinkedHashSet<>(), new LinkedHashSet<>(), new LinkedHashSet<>());
        for (Group group : contest.getGroups()) {
            this.groups.add(new GroupDto(group.getId(), group.getName()));
        }
        for (User user : contest.getUsers()) {
            this.users.add(new UserDto(user.getId(), user.getUsername(), user.getEmail()));
        }
        for (ContestProblem contestProblem : contest.getContestProblems()) {
            this.contestProblems.add(new ContestProblemDto(contestProblem.getId(), contestProblem.getTitle(), contestProblem.getContents(), contestProblem.getUseAutograding(), contestProblem.getUseAutogradingAnswer(), contestProblem.getDeadline()));
        }
    }

    public Integer getId() {
        return id;
    }

    public ContestDto setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public ContestDto setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ContestDto setDescription(String description) {
        this.description = description;
        return this;
    }

    public Boolean getIsPublished() {
        return isPublished;
    }

    public ContestDto setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
        return this;
    }

    public Set<GroupDto> getGroups() {
        return groups;
    }

    public ContestDto setGroups(Set<GroupDto> groups) {
        this.groups = groups;
        return this;
    }

    public Set<ContestProblemDto> getContestProblems() {
        return contestProblems;
    }

    public ContestDto setContestProblems(Set<ContestProblemDto> contestProblems) {
        this.contestProblems = contestProblems;
        return this;
    }

    public Set<UserDto> getUsers() {
        return users;
    }

    public ContestDto setUsers(Set<UserDto> users) {
        this.users = users;
        return this;
    }
}