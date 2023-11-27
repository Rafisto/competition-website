package org.contesthub.apiserver.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

@Valid
public class CreateContestRequest {
    @NotBlank
    @Length(min=3)
    private String name;

    private String description;

    private Boolean isPublished;

    private Integer[] groups;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer[] getGroups() {
        return groups;
    }

    public void setGroups(Integer[] groups) {
        this.groups = groups;
    }

    public boolean isValid() {
        if (isPublished == null) {
            this.isPublished = Boolean.FALSE;
        }
        return name != null && name.length() >= 3;
    }
}
