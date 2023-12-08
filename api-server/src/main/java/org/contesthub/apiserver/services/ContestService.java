package org.contesthub.apiserver.services;

import jakarta.persistence.EntityNotFoundException;
import org.contesthub.apiserver.databaseInterface.DTOs.ContestDto;
import org.contesthub.apiserver.databaseInterface.DTOs.GroupDto;
import org.contesthub.apiserver.databaseInterface.models.Contest;
import org.contesthub.apiserver.databaseInterface.models.User;
import org.contesthub.apiserver.databaseInterface.repositories.ContestRepository;
import org.contesthub.apiserver.databaseInterface.repositories.GroupRepository;
import org.contesthub.apiserver.databaseInterface.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ContestService {
    @Autowired
    private ContestRepository contestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    public List<Contest> loadContestsUserCanJoin(UserDetailsImpl user) {
        List<Contest> contests = new ArrayList<>();
        for(GroupDto group : user.getGroups()) {
            contests.addAll(contestRepository.findByGroupsContainsAndIsPublishedTrue(groupRepository.getReferenceById(group.getId())));
        }
        return contests;
    }

    public List<Contest> loadContestByProblem(Integer problemId) {
        return null;
    }

    public List<Contest> loadUserContests(UserDetailsImpl user){
        User dbUser = userRepository.findByUsername(user.getUsername()).orElseThrow(null);
        return contestRepository.findByUsersContainsAndIsPublishedTrue(dbUser);
    }
}
