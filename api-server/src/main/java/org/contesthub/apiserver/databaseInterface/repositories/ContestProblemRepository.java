package org.contesthub.apiserver.databaseInterface.repositories;

import org.contesthub.apiserver.databaseInterface.models.Contest;
import org.contesthub.apiserver.databaseInterface.models.ContestProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ContestProblemRepository extends JpaRepository<ContestProblem, Integer>, JpaSpecificationExecutor<ContestProblem> {
    ContestProblem findByContest(Contest contestId);

    List<ContestProblem> findAllByContestId(Integer contestId);
}