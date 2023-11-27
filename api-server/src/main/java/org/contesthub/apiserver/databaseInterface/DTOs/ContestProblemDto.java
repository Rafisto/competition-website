package org.contesthub.apiserver.databaseInterface.DTOs;

import org.contesthub.apiserver.databaseInterface.models.ContestGrading;
import org.contesthub.apiserver.databaseInterface.models.ContestProblem;

import java.io.Serializable;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * DTO for {@link ContestProblem}
 */
public class ContestProblemDto implements Serializable {
    private Integer id;
    private String title;
    private String contents;
    private Boolean useAutograding;
    private String useAutogradingAnswer;
    private Instant deadline;
    private ContestDto contest;
    private Set<ContestGradingDto> contestGradings;

    public ContestProblemDto() {
    }

    public ContestProblemDto(ContestProblem contestProblem) {
        this.id = contestProblem.getId();
        this.title = contestProblem.getTitle();
        this.contents = contestProblem.getContents();
        this.useAutograding = contestProblem.getUseAutograding();
        this.useAutogradingAnswer = contestProblem.getUseAutogradingAnswer();
        this.deadline = contestProblem.getDeadline();
        this.contest = new ContestDto(contestProblem.getContest().getId(), contestProblem.getContest().getTitle(), contestProblem.getContest().getDescription(), contestProblem.getContest().getIsPublished());
        this.contestGradings = new LinkedHashSet<>();
        for (ContestGrading contestGrading : contestProblem.getContestGradings()) {
            this.contestGradings.add(new ContestGradingDto(contestGrading.getScore(), new UserDto(contestGrading.getUser().getId(), contestGrading.getUser().getUsername(), contestGrading.getUser().getEmail())));
        }
    }

    public ContestProblemDto(Integer id, String title, String contents, Boolean useAutograding, String useAutogradingAnswer, Instant deadline, ContestDto contest, Set<ContestGradingDto> contestGradings) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.useAutograding = useAutograding;
        this.useAutogradingAnswer = useAutogradingAnswer;
        this.deadline = deadline;
        this.contest = contest;
        this.contestGradings = contestGradings;
    }

    public ContestProblemDto(Integer id, String title, String contents, Boolean useAutograding, String useAutogradingAnswer, Instant deadline) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.useAutograding = useAutograding;
        this.useAutogradingAnswer = useAutogradingAnswer;
        this.deadline = deadline;
    }

    public Integer getId() {
        return id;
    }

    public ContestProblemDto setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public ContestProblemDto setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContents() {
        return contents;
    }

    public ContestProblemDto setContents(String contents) {
        this.contents = contents;
        return this;
    }

    public Boolean getUseAutograding() {
        return useAutograding;
    }

    public ContestProblemDto setUseAutograding(Boolean useAutograding) {
        this.useAutograding = useAutograding;
        return this;
    }

    public String getUseAutogradingAnswer() {
        return useAutogradingAnswer;
    }

    public ContestProblemDto setUseAutogradingAnswer(String useAutogradingAnswer) {
        this.useAutogradingAnswer = useAutogradingAnswer;
        return this;
    }

    public Instant getDeadline() {
        return deadline;
    }

    public ContestProblemDto setDeadline(Instant deadline) {
        this.deadline = deadline;
        return this;
    }

    public ContestDto getContest() {
        return contest;
    }

    public ContestProblemDto setContest(ContestDto contest) {
        this.contest = contest;
        return this;
    }

    public Set<ContestGradingDto> getContestGradings() {
        return contestGradings;
    }

    public ContestProblemDto setContestGradings(Set<ContestGradingDto> contestGradings) {
        this.contestGradings = contestGradings;
        return this;
    }
}