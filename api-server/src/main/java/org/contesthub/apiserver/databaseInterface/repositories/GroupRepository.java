package org.contesthub.apiserver.databaseInterface.repositories;

import org.contesthub.apiserver.databaseInterface.models.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GroupRepository extends JpaRepository<Group, Integer>, JpaSpecificationExecutor<Group> {
}