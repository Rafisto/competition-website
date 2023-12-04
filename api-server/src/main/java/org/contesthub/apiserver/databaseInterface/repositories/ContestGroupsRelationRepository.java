package org.contesthub.apiserver.databaseInterface.repositories;

import org.contesthub.apiserver.databaseInterface.models.ContestGroupsRelation;
import org.contesthub.apiserver.databaseInterface.models.ContestGroupsRelationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ContestGroupsRelationRepository extends JpaRepository<ContestGroupsRelation, ContestGroupsRelationId>, JpaSpecificationExecutor<ContestGroupsRelation> {
}