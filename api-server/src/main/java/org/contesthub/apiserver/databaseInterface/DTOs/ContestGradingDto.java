package org.contesthub.apiserver.databaseInterface.DTOs;

import org.contesthub.apiserver.databaseInterface.models.ContestGrading;

import java.io.Serializable;

/**
 * DTO for {@link ContestGrading}
 */
public class ContestGradingDto implements Serializable {
    private ContestProblemDto problem;
    private Integer score;
    private UserDto user;

    public ContestGradingDto() {
    }

    public ContestGradingDto(ContestGrading contestGrading) {
        this.score = contestGrading.getScore();
        this.problem = new ContestProblemDto(contestGrading.getProblem().getId(),
                                             contestGrading.getProblem().getTitle(),
                                             contestGrading.getProblem().getContents(),
                                             contestGrading.getProblem().getUseAutograding(),
                                             contestGrading.getProblem().getUseAutogradingAnswer(),
                                             contestGrading.getProblem().getDeadline());
        this.user = new UserDto(contestGrading.getUser().getId(), contestGrading.getUser().getUsername(), contestGrading.getUser().getEmail());
    }

    public ContestGradingDto(Integer score, UserDto user) {
        this.score = score;
        this.user = user;
    }

    public ContestGradingDto(ContestProblemDto problem, UserDto user, Integer score) {
        this.problem = problem;
        this.score = score;
        this.user = user;
    }

    public ContestProblemDto getProblem() {
        return problem;
    }

    public ContestGradingDto setProblem(ContestProblemDto problem) {
        this.problem = problem;
        return this;
    }

    public Integer getScore() {
        return score;
    }

    public ContestGradingDto setScore(Integer score) {
        this.score = score;
        return this;
    }

    public UserDto getUser() {
        return user;
    }

    public ContestGradingDto setUser(UserDto user) {
        this.user = user;
        return this;
    }
}