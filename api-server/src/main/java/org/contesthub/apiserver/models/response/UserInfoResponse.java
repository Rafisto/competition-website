package org.contesthub.apiserver.models.response;

import org.contesthub.apiserver.databaseInterface.DTOs.ContestDto;
import org.contesthub.apiserver.databaseInterface.DTOs.GroupDto;
import org.contesthub.apiserver.databaseInterface.DTOs.UserDto;
import org.contesthub.apiserver.services.UserDetailsImpl;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class UserInfoResponse {
    private String username;
    private Set<ShortContestResponse> contests = new LinkedHashSet<>();
    private Set<ShortGroupResponse> groups = new LinkedHashSet<>();


    public UserInfoResponse() {
    }

    public UserInfoResponse(String username) {
        this.username = username;
    }

    public UserInfoResponse(UserDto user){
        this.username = user.getUsername();
        this.contests = user.getContests().stream().map(ShortContestResponse::new).collect(Collectors.toSet());
        this.groups = user.getGroups().stream().map(ShortGroupResponse::new).collect(Collectors.toSet());
    }

    public UserInfoResponse(String username, Set<ContestDto> contests, Set<GroupDto> groups) {
        this.username = username;
        this.contests = contests.stream().map(ShortContestResponse::new).collect(Collectors.toSet());
        this.groups = groups.stream().map(ShortGroupResponse::new).collect(Collectors.toSet());
    }

    public UserInfoResponse(UserDetailsImpl user){
        this.username = user.getUsername();
        this.contests = user.getUser().getContests().stream().map(contest ->
                new ShortContestResponse(contest.getId(), contest.getTitle(), contest.getDescription(), contest.getIsPublished())
        ).collect(Collectors.toSet());
        this.groups = user.getUser().getGroups().stream().map(group -> new ShortGroupResponse(group.getId(), group.getName())).collect(Collectors.toSet());
    }

    // Getters and setters
    static class ShortContestResponse {
        private Integer id;
        private String title;
        private String description;
        private Boolean isPublished;

        public ShortContestResponse() {
        }

        public ShortContestResponse(Integer id, String title, String description, Boolean isPublished) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.isPublished = isPublished;
        }

        public ShortContestResponse(ContestDto contest) {
            this.id = contest.getId();
            this.title = contest.getTitle();
            this.description = contest.getDescription();
            this.isPublished = contest.getIsPublished();
        }

        // Getters and setters
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Boolean getPublished() {
            return isPublished;
        }

        public void setPublished(Boolean published) {
            isPublished = published;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }
    }

    static class ShortGroupResponse {
        private Integer id;
        private String name;

        public ShortGroupResponse() {
        }

        public ShortGroupResponse(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        public ShortGroupResponse(GroupDto group) {
            this.id = group.getId();
            this.name = group.getName();
        }

        // Getters and setters
        public Integer getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<ShortContestResponse> getContests() {
        return contests;
    }

    public void setContests(Set<ShortContestResponse> contests) {
        this.contests = contests;
    }

    public Set<ShortGroupResponse> getGroups() {
        return groups;
    }

    public void setGroups(Set<ShortGroupResponse> groups) {
        this.groups = groups;
    }
}
