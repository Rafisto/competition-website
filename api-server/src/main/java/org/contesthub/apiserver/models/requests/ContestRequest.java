package org.contesthub.apiserver.models.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

/***
 * Matches both the request for creation and update of a contest
 * Update should not call validation on this object, as empty fields should not be overwritten
 */
public class ContestRequest {
    @NotBlank
    @Length(min=3)
    private String title;

    @NotNull
    @Length(min=3)
    private String description;

    private Boolean isPublished;

    private Integer[] groups;

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
        return (title != null && title.length() >= 3) && (description != null && description.length() >= 3);
    }
}
