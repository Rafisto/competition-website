package org.contesthub.apiserver.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

@Valid
public class CreateContestProblemRequest {
    @NotBlank
    @Size(min=3)
    private String title;

    private String contents;

    private Boolean useAutograding;

    private String useAutogradingAnswer;

    @AssertTrue
    public boolean checkAutograding() {
        return !useAutograding || useAutogradingAnswer != null;
    }

    @NotNull
    private Instant deadline;

    @NotNull
    private Integer contest_id;
}
