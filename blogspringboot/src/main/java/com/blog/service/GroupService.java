package com.blog.service;

import com.blog.entity.Group;

import java.util.List;

public interface GroupService {

    void insert(Integer groupId, List<Integer> userIds);

    void creat(Group group);

    void deleteGroup(Integer groupId);

    void deleteGroupUser(List<Integer> userIds);

    List<Group> getGroup(Integer userid);
}
