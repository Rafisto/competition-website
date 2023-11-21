package org.contesthub.apiserver.repositories;

import org.contesthub.apiserver.models.UserGroupRelation;
import org.contesthub.apiserver.models.UserGroupRelationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserGroupRelationRepository extends JpaRepository<UserGroupRelation, UserGroupRelationId>, JpaSpecificationExecutor<UserGroupRelation> {
}