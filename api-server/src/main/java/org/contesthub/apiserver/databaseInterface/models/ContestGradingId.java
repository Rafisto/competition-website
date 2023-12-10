package org.contesthub.apiserver.databaseInterface.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ContestGradingId implements Serializable {
    private static final long serialVersionUID = 7813785512162280494L;
    @NotNull
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @NotNull
    @Column(name = "problem_id", nullable = false)
    private Integer problemId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getProblemId() {
        return problemId;
    }

    public void setProblemId(Integer problemId) {
        this.problemId = problemId;
    }

    public ContestGradingId() {
    }

    public ContestGradingId(Integer userId, Integer problemId) {
        this.userId = userId;
        this.problemId = problemId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ContestGradingId entity = (ContestGradingId) o;
        return Objects.equals(this.problemId, entity.problemId) &&
                Objects.equals(this.userId, entity.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(problemId, userId);
    }

}