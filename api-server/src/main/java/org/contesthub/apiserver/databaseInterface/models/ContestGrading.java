package org.contesthub.apiserver.databaseInterface.models;

import jakarta.persistence.*;

@Entity
@Table(name = "contest_grading")
public class ContestGrading {
    @EmbeddedId
    private ContestGradingId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @MapsId("problemId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "problem_id", nullable = false)
    private ContestProblem problem;

    @Column(name = "score")
    private Integer score;

    public ContestGradingId getId() {
        return id;
    }

    public void setId(ContestGradingId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ContestProblem getProblem() {
        return problem;
    }

    public void setProblem(ContestProblem problem) {
        this.problem = problem;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

}