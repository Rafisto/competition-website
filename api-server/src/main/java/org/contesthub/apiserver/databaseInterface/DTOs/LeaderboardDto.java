package org.contesthub.apiserver.databaseInterface.DTOs;

import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.SqlResultSetMapping;
import org.springframework.data.jpa.repository.Query;

public class LeaderboardDto {
    private String username;
    private int score;

    public LeaderboardDto(String username, int score) {
        this.username = username;
        this.score = score;
    }

    public LeaderboardDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
