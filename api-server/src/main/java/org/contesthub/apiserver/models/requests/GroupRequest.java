package org.contesthub.apiserver.models.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class GroupRequest {
    @NotNull
    @Size(min = 4, max = 255)
    private String name;
    private Integer[] contests;
    private Integer[] users;

    public Boolean isValid() {
        this.name = this.name.strip();
        return name != null && name.length() > 3;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer[] getContests() {
        return contests;
    }

    public void setContests(Integer[] contests) {
        this.contests = contests;
    }

    public Integer[] getUsers() {
        return users;
    }

    public void setUsers(Integer[] users) {
        this.users = users;
    }
}
