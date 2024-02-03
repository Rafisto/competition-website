package org.contesthub.apiserver.models.response;

import jakarta.validation.constraints.Size;
import org.contesthub.apiserver.databaseInterface.DTOs.ContestDto;

import java.util.Set;

public class ContestResponse {
    private Integer id;
    private String title;
    private String description;
    private Boolean isPublished;
    private Boolean joinable;

    public ContestResponse() {
    }

    public ContestResponse(ContestDto contest, boolean joinable) {
        this.id = contest.getId();
        this.title = contest.getTitle();
        this.description = contest.getDescription();
        this.isPublished = contest.getIsPublished();
        this.joinable = joinable;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getPublished() {
        return isPublished;
    }

    public void setPublished(Boolean published) {
        isPublished = published;
    }

    public Boolean getJoinable() {
        return joinable;
    }

    public void setJoinable(Boolean joinable) {
        this.joinable = joinable;
    }
}
