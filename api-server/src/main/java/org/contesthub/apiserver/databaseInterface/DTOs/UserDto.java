package org.contesthub.apiserver.databaseInterface.DTOs;

import jakarta.validation.constraints.Size;
import org.contesthub.apiserver.databaseInterface.models.Contest;
import org.contesthub.apiserver.databaseInterface.models.ContestGrading;
import org.contesthub.apiserver.databaseInterface.models.Group;
import org.contesthub.apiserver.databaseInterface.models.User;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * DTO for {@link User}
 */
public class UserDto implements Serializable {
    private Integer id;
    @Size(max = 255)
    private String username;
    @Size(max = 255)
    private String email;
    private Set<ContestGradingDto> contestGradings = new LinkedHashSet<>();
    private Set<ContestDto> contests = new LinkedHashSet<>();
    private Set<GroupDto> groups = new LinkedHashSet<>();

    public UserDto() {
    }

    public UserDto(User user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.contestGradings = new LinkedHashSet<>();
        for (ContestGrading contestGrading : user.getContestGradings()) {
            this.contestGradings.add(new ContestGradingDto(contestGrading.getScore(), new UserDto(contestGrading.getUser().getId(), contestGrading.getUser().getUsername(), contestGrading.getUser().getEmail())));
        }
        this.contests = new LinkedHashSet<>();
        for (Contest contest : user.getContests()) {
            this.contests.add(new ContestDto(contest.getId(), contest.getTitle(), contest.getDescription(), contest.getIsPublished()));
        }
        this.groups = new LinkedHashSet<>();
        for (Group group : user.getGroups()) {
            this.groups.add(new GroupDto(group.getId(), group.getName()));
        }
    }

    public UserDto(Integer id, String username, String email, Set<ContestGradingDto> contestGradings, Set<ContestDto> contests, Set<GroupDto> groups) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.contestGradings = contestGradings;
        this.contests = contests;
        this.groups = groups;
    }

    public UserDto(Integer id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public UserDto setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public UserDto setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public Set<ContestGradingDto> getContestGradings() {
        return contestGradings;
    }

    public UserDto setContestGradings(Set<ContestGradingDto> contestGradings) {
        this.contestGradings = contestGradings;
        return this;
    }

    public Set<ContestDto> getContests() {
        return contests;
    }

    public UserDto setContests(Set<ContestDto> contests) {
        this.contests = contests;
        return this;
    }
}