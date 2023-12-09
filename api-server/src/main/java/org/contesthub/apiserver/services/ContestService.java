package org.contesthub.apiserver.services;

import jakarta.persistence.EntityNotFoundException;
import org.contesthub.apiserver.databaseInterface.DTOs.ContestDto;
import org.contesthub.apiserver.databaseInterface.DTOs.GroupDto;
import org.contesthub.apiserver.databaseInterface.models.*;
import org.contesthub.apiserver.databaseInterface.repositories.ContestRepository;
import org.contesthub.apiserver.databaseInterface.repositories.GroupRepository;
import org.contesthub.apiserver.databaseInterface.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ContestService {
    @Autowired
    private ContestRepository contestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    public List<Contest> loadContestsUserCanJoin(UserDetailsImpl user) {
        // TODO: this needs thorough testing
        List<Contest> contests = new ArrayList<>();
        for(Group group : user.getUser().getGroups()) {
            contests.addAll(contestRepository.findByGroupsContainsAndIsPublishedTrue(group));
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

    public Set<Contest> loadContestsFromIdList(Integer[] contestIds) {
        return contestRepository.findAllByIdIn(contestIds);
    }
}
