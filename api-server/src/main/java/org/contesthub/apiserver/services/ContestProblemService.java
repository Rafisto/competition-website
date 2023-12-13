package org.contesthub.apiserver.services;

import jakarta.persistence.EntityNotFoundException;
import org.contesthub.apiserver.databaseInterface.models.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;

@Service
public class ContestProblemService {
    @Transactional
    public ContestProblem findMatchingProblem(User user, int problemId) {
        return user.getContests().stream()
                .map(Contest::getContestProblems).reduce(new LinkedHashSet<ContestProblem>(), (reducedSet, newSet) -> {
                    reducedSet.addAll(newSet);
                    return reducedSet;
                }).stream().filter(contestProblem -> contestProblem.getId().equals(problemId)).findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Contest Not found"));
    }
}
