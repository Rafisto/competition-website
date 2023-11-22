package org.contesthub.apiserver.databaseInterface.repositories;

import org.contesthub.apiserver.databaseInterface.models.ContestGrading;
import org.contesthub.apiserver.databaseInterface.models.ContestGradingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ContestGradingRepository extends JpaRepository<ContestGrading, ContestGradingId>, JpaSpecificationExecutor<ContestGrading> {
}