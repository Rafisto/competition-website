package org.contesthub.apiserver.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @Column(name = "username")
    private String username;

    @Size(max = 255)
    @Column(name = "email")
    private String email;

    @OneToMany(mappedBy = "user")
    private Set<ContestGrading> contestGradings = new LinkedHashSet<>();

    @ManyToMany(mappedBy = "user")
    private Set<Group> groups = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<ContestGrading> getContestGradings() {
        return contestGradings;
    }

    public void setContestGradings(Set<ContestGrading> contestGradings) {
        this.contestGradings = contestGradings;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

}