package org.contesthub.apiserver.databaseInterface.repositories;

import org.contesthub.apiserver.databaseInterface.models.UserGroupRelation;
import org.contesthub.apiserver.databaseInterface.models.UserGroupRelationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserGroupRelationRepository extends JpaRepository<UserGroupRelation, UserGroupRelationId>, JpaSpecificationExecutor<UserGroupRelation> {
}