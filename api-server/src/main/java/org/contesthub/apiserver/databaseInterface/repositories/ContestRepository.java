package org.contesthub.apiserver.databaseInterface.repositories;

import org.contesthub.apiserver.databaseInterface.models.Contest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ContestRepository extends JpaRepository<Contest, Integer>, JpaSpecificationExecutor<Contest> {
    @Transactional
    @Modifying
    @Query("update Contest c set c.isPublished = ?1 where c.title like ?2")
    void updateIsPublishedByTitleLike(boolean isPublished, String title);
    @Transactional
    @Modifying
    @Query("update Contest c set c.isPublished = ?1 where c.id = ?2")
    void updateIsPublishedById(boolean isPublished, Integer id);

    List<Contest> findByIsPublishedTrue();
    List<Contest> findByIsPublishedFalse();

    Optional<Contest> findById(Integer id);
    Contest findByTitle(String title);

    // Problems that are children of the contest are still there, with the contest_id field set to null
    void deleteById(Integer contestId);

//    TODO: insert contest into table
//    Contest

    @Transactional
    @Query("SELECT c FROM Contest c")
    List<Contest> getAll();
}