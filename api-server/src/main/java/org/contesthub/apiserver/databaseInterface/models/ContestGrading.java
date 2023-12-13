package org.contesthub.apiserver.databaseInterface.models;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.Instant;

@Entity
@Table(name = "contest_grading")
@DynamicInsert
@DynamicUpdate
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

    @Column(name = "is_file")
    private Boolean isFile;

    @Column(name = "submittedAt")
    private Instant submittedAt;

    @Column(name = "last_updated_at")
    private Instant lastUpdatedAt;

    public ContestGrading() {
    }

    public ContestGrading(User user, ContestProblem problem, String answer, Boolean isFile) {
        this.user = user;
        this.problem = problem;
        this.answer = answer;
        this.isFile = isFile;
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

    public Boolean getIsFile() {
        return isFile;
    }

    public void setIsFile(Boolean file) {
        isFile = file;
    }

    public Instant getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(Instant lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
}