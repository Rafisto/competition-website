package org.contesthub.apiserver.repositories;

import org.contesthub.apiserver.models.ContestGroupsRelation;
import org.contesthub.apiserver.models.ContestGroupsRelationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ContestGroupsRelationRepository extends JpaRepository<ContestGroupsRelation, ContestGroupsRelationId>, JpaSpecificationExecutor<ContestGroupsRelation> {
}