package org.contesthub.apiserver.databaseInterface.DTOs;

import jakarta.validation.constraints.Size;
import org.contesthub.apiserver.databaseInterface.models.Contest;
import org.contesthub.apiserver.databaseInterface.models.Group;
import org.contesthub.apiserver.databaseInterface.models.User;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * DTO for {@link Group}
 */
public class GroupDto implements Serializable {
    private Integer id;
    @Size(max = 255)
    private String name;
    private Set<ContestDto> contests;
    private Set<UserDto> users;

    public GroupDto() {
    }

    public GroupDto(Group group) {
        this.id = group.getId();
        this.name = group.getName();
        this.users = new LinkedHashSet<>();
        for(User user : group.getUsers()) {
            this.users.add(new UserDto(user.getId(), user.getUsername(), user.getEmail()));
        }
        this.contests = new LinkedHashSet<>();
        for(Contest contest : group.getContests()) {
            this.contests.add(new ContestDto(contest.getId(), contest.getTitle(), contest.getDescription(), contest.getIsPublished()));
        }
    }

    public GroupDto(Integer id, String name, Set<ContestDto> contests, Set<UserDto> users) {
        this.id = id;
        this.name = name;
        this.contests = contests;
        this.users = users;
    }

    public GroupDto(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public GroupDto setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public GroupDto setName(String name) {
        this.name = name;
        return this;
    }

    public Set<ContestDto> getContests() {
        return contests;
    }

    public GroupDto setContests(Set<ContestDto> contests) {
        this.contests = contests;
        return this;
    }

    public Set<UserDto> getUsers() {
        return users;
    }

    public GroupDto setUsers(Set<UserDto> users) {
        this.users = users;
        return this;
    }
}