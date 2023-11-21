package org.contesthub.apiserver.repositories;

import org.contesthub.apiserver.models.ContestProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ContestProblemRepository extends JpaRepository<ContestProblem, Integer>, JpaSpecificationExecutor<ContestProblem> {
}