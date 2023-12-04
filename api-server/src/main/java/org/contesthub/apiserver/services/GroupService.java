package org.contesthub.apiserver.services;

import jakarta.persistence.EntityNotFoundException;
import org.contesthub.apiserver.databaseInterface.models.Group;
import org.contesthub.apiserver.databaseInterface.repositories.GroupRepository;
import org.contesthub.apiserver.databaseInterface.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    /***
     * Generate set of Group Entities from database based of list of their Ids
     * @param groupIds List of group Ids to be converted
     * @return Set of Group Entities with given ids
     */
    public Set<Group> loadGroupsFromIdList(Integer[] groupIds) {
        Set<Group> groupObjectSet = new LinkedHashSet<>();
        for(Integer group : groupIds) {
            Group groupObject = groupRepository.findById(group).orElseThrow(() ->
                    new EntityNotFoundException("Could not find group with id " + group));
            groupObjectSet.add(groupObject);
        }
        return groupObjectSet;
    }
}
