package org.contesthub.apiserver.repositories;

import org.contesthub.apiserver.models.ContestGrading;
import org.contesthub.apiserver.models.ContestGradingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ContestGradingRepository extends JpaRepository<ContestGrading, ContestGradingId>, JpaSpecificationExecutor<ContestGrading> {
}