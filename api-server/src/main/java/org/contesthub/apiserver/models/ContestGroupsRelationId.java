package org.contesthub.apiserver.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ContestGroupsRelationId implements Serializable {
    private static final long serialVersionUID = 7362501992295217751L;
    @NotNull
    @Column(name = "contest_id", nullable = false)
    private Integer contestId;

    @NotNull
    @Column(name = "group_id", nullable = false)
    private Integer groupId;

    public Integer getContestId() {
        return contestId;
    }

    public void setContestId(Integer contestId) {
        this.contestId = contestId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ContestGroupsRelationId entity = (ContestGroupsRelationId) o;
        return Objects.equals(this.contestId, entity.contestId) &&
                Objects.equals(this.groupId, entity.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contestId, groupId);
    }

}