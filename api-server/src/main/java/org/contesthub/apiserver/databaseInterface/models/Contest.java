package org.contesthub.apiserver.databaseInterface.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.contesthub.apiserver.databaseInterface.repositories.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.web.Link;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "contests")
public class Contest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @Column(name = "title")
    private String title;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Column(name = "is_published")
    private Boolean isPublished;

    @ManyToMany
    @JoinTable(name = "contest_groups_relations",
            joinColumns = @JoinColumn(name = "contest_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Set<Group> groups = new LinkedHashSet<>();

    @OneToMany
    @JoinColumn(name="contest_id")
    private Set<ContestProblem> contestProblems = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "contest_user_relations",
            joinColumns = @JoinColumn(name = "contest_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users = new LinkedHashSet<>();

    public Contest(String title, String description, Boolean isPublished, Set<Group> groups){
        this.title = title;
        this.description = description;
        this.isPublished = isPublished;
        this.groups = groups;
    }

    public Contest() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    public Set<ContestProblem> getContestProblems() {
        return contestProblems;
    }

    public void setContestProblems(Set<ContestProblem> contestProblems) {
        this.contestProblems = contestProblems;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

}