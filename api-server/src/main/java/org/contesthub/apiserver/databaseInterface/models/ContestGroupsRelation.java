package org.contesthub.apiserver.databaseInterface.models;

import jakarta.persistence.*;

@Entity
@Table(name = "contest_groups_relations")
public class ContestGroupsRelation {
    @EmbeddedId
    private ContestGroupsRelationId id;

    @MapsId("contestId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contest_id", nullable = false)
    private Contest contest;

    @MapsId("groupId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    public ContestGroupsRelationId getId() {
        return id;
    }

    public void setId(ContestGroupsRelationId id) {
        this.id = id;
    }

    public Contest getContest() {
        return contest;
    }

    public void setContest(Contest contest) {
        this.contest = contest;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

}