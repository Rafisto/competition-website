package org.contesthub.apiserver.models.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

/***
 * Matches both the request for creation and update of a problem
 * Update should not call validation on this object, as empty fields should not be overwritten
 *
 * @attr title Title of the problem
 * @attr contents Contents of the problem
 * @attr useAutograding Whether to use autograding
 * @attr useAutogradingAnswer Answer for autograding
 * @attr deadline Deadline of the problem, datetime in ISO 8601 format
 */
@Valid
public class ContestProblemRequest {
    @NotBlank
    @Size(min=3)
    private String title;

    @NotBlank
    private String contents;

    private Boolean useAutograding;

    private String useAutogradingAnswer;

    @AssertTrue
    public boolean checkAutograding() {
        return !useAutograding || (useAutogradingAnswer != null && !useAutogradingAnswer.isEmpty());
    }

    @NotNull
    private Instant deadline;

    @AssertTrue
    public boolean checkDeadline() {
        return deadline.isAfter(Instant.now());
    }

    public boolean isValid() {
        if (!checkAutograding()){
            return false;
        }
        if (deadline == null || !checkDeadline()){
            throw new IllegalArgumentException("Deadline cannot be in the past");
        }
        if (title == null || title.length() < 3){
            return false;
        }
        return true;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public Boolean getUseAutograding() {
        return useAutograding;
    }

    public void setUseAutograding(Boolean useAutograding) {
        this.useAutograding = useAutograding;
    }

    public String getUseAutogradingAnswer() {
        return useAutogradingAnswer;
    }

    public void setUseAutogradingAnswer(String useAutogradingAnswer) {
        this.useAutogradingAnswer = useAutogradingAnswer;
    }

    public Instant getDeadline() {
        return deadline;
    }

    public void setDeadline(Instant deadline) {
        this.deadline = deadline;
    }
}
