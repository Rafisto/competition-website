package org.contesthub.apiserver.databaseInterface.models;

import jakarta.persistence.*;

import java.time.Instant;

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

    @Column(name = "answer")
    private String answer;

    @Column(name = "submittedAt")
    private Instant submittedAt;

    public ContestGrading() {
    }

    public ContestGrading(User user, ContestProblem problem, String answer) {
        this.user = user;
        this.problem = problem;
        this.answer = answer;
    }

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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }
}