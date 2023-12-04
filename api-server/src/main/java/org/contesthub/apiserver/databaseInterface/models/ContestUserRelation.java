package org.contesthub.apiserver.databaseInterface.models;

import jakarta.persistence.*;

@Entity
@Table(name = "contest_user_relations")
public class ContestUserRelation {
    @EmbeddedId
    private ContestUserRelationId id;

    @MapsId("contestId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contest_id", nullable = false)
    private Contest contest;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public ContestUserRelationId getId() {
        return id;
    }

    public void setId(ContestUserRelationId id) {
        this.id = id;
    }

    public Contest getContest() {
        return contest;
    }

    public void setContest(Contest contest) {
        this.contest = contest;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}