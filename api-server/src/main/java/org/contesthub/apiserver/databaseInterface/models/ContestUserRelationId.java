package org.contesthub.apiserver.databaseInterface.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ContestUserRelationId implements Serializable {
    private static final long serialVersionUID = -6633580547347433264L;
    @NotNull
    @Column(name = "contest_id", nullable = false)
    private Integer contestId;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    public Integer getContestId() {
        return contestId;
    }

    public void setContestId(Integer contestId) {
        this.contestId = contestId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ContestUserRelationId entity = (ContestUserRelationId) o;
        return Objects.equals(this.contestId, entity.contestId) &&
                Objects.equals(this.userId, entity.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contestId, userId);
    }

}