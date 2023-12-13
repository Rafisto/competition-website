package org.contesthub.apiserver.databaseInterface.repositories;

import jakarta.persistence.SqlResultSetMapping;
import org.contesthub.apiserver.databaseInterface.DTOs.ContestGradingDto;
import org.contesthub.apiserver.databaseInterface.DTOs.LeaderboardDto;
import org.contesthub.apiserver.databaseInterface.models.ContestGrading;
import org.contesthub.apiserver.databaseInterface.models.ContestGradingId;
import org.contesthub.apiserver.databaseInterface.models.ContestProblem;
import org.contesthub.apiserver.databaseInterface.models.User;
import org.springframework.beans.PropertyValues;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ContestGradingRepository extends JpaRepository<ContestGrading, ContestGradingId>, JpaSpecificationExecutor<ContestGrading> {
    List<ContestGrading> findByProblem_Contest_IdAndUser(Integer id, User user);
    @Transactional
    ContestGrading findByUserAndProblem(User user, ContestProblem problem);

    @Transactional
    @Query(value="SELECT u.username, COALESCE(SUM(cg.score), 0) AS score FROM users u LEFT JOIN contest_grading cg ON u.id = cg.user_id GROUP BY u.id ORDER BY score DESC",
            nativeQuery = true)
    Object[][] getLeaderboard();

    @Transactional
    @Query(value="SELECT u.username, COALESCE(SUM(cg.score), 0) AS score FROM users u LEFT JOIN contest_grading cg ON u.id = cg.user_id LEFT JOIN contest_problems cp ON cg.problem_id = cp.id WHERE cp.contest_id = ?1 GROUP BY u.id, cp.id ORDER BY score DESC",
            nativeQuery = true)
    Object[][] getLeaderboardByContestId(int contestId);

    @Transactional
    Set<ContestGrading> findByProblem(ContestProblem problem);

    @Transactional
    Set<ContestGrading> findByUser(User user);

    Set<ContestGrading> findByUserAndProblem_DeadlineAfter(User user, Instant deadline);

    Set<ContestGrading> findByProblem_Id(Integer problemId);

    Optional<ContestGrading> findByProblem_IdAndUser(Integer problemId, User user);
}