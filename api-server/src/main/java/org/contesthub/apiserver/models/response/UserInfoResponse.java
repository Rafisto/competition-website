package org.contesthub.apiserver.models.response;

import org.contesthub.apiserver.databaseInterface.DTOs.ContestDto;
import org.contesthub.apiserver.databaseInterface.DTOs.GroupDto;
import org.contesthub.apiserver.databaseInterface.DTOs.UserDto;
import org.contesthub.apiserver.services.UserDetailsImpl;

import java.util.LinkedHashSet;
import java.util.Set;

public class UserInfoResponse {
    private String username;
    private Set<ContestDto> contests = new LinkedHashSet<>();
    private Set<GroupDto> groups = new LinkedHashSet<>();


    public UserInfoResponse() {
    }

    public UserInfoResponse(String username) {
        this.username = username;
    }

    public UserInfoResponse(UserDto user){
        this.username = user.getUsername();
        this.contests = user.getContests();
        this.groups = user.getGroups();
    }

    public UserInfoResponse(String username, Set<ContestDto> contests, Set<GroupDto> groups) {
        this.username = username;
        this.contests = contests;
        this.groups = groups;
    }

    public UserInfoResponse(UserDetailsImpl user){
        this.username = user.getUsername();
        this.contests = user.getContests();
        this.groups = user.getGroups();
    }

    // Getters and setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<ContestDto> getContests() {
        return contests;
    }

    public void setContests(Set<ContestDto> contests) {
        this.contests = contests;
    }

    public Set<GroupDto> getGroups() {
        return groups;
    }

    public void setGroups(Set<GroupDto> groups) {
        this.groups = groups;
    }
}
