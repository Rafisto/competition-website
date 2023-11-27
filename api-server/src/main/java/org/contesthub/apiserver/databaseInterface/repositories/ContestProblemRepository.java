package org.contesthub.apiserver.databaseInterface.repositories;

import org.contesthub.apiserver.databaseInterface.models.Contest;
import org.contesthub.apiserver.databaseInterface.models.ContestProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ContestProblemRepository extends JpaRepository<ContestProblem, Integer>, JpaSpecificationExecutor<ContestProblem> {
    @Transactional
    @Modifying
    @Query("update ContestProblem c set c.title = ?1 where c.id = ?2")
    void updateTitleById(String title, Integer contestProblemId);

    @Transactional
    @Modifying
    @Query("update ContestProblem c set c.contents = ?1 where c.id = ?2")
    void updateContentsById(String contents, Integer contestProblemId);

    @Transactional
    @Modifying
    @Query("update ContestProblem c set c.useAutograding = ?1 where c.id = ?2")
    void updateUseAutogradingById(Boolean useAutograding, Integer contestProblemId);

    @Transactional
    @Modifying
    @Query("update ContestProblem c set c.useAutogradingAnswer = ?1 where c.id = ?2")
    void updateUseAutogradingAnswerById(String useAutogradingAnswer, Integer contestProblemId);

    @Transactional
    @Modifying
    @Query("update ContestProblem c set c.deadline = ?1 where c.id = ?2")
    void updateDeadlineById(Instant deadline, Integer contestProblemId);

    Optional<ContestProblem> findById(Integer id);

    ContestProblem findByContest(Contest contestId);

    List<ContestProblem> findAllByContestId(Integer contestId);
}