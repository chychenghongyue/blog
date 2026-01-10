package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.blog.entity.Group;
import com.blog.entity.GroupUser;
import com.blog.mapper.GroupMapper;
import com.blog.mapper.GroupUserMapper;
import com.blog.service.GroupService;
import com.github.houbb.heaven.util.lang.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GroupServiceImpl implements GroupService {
    @Autowired
    GroupMapper groupMapper;
    @Autowired
    GroupUserMapper groupUserMapper;

    @Override
    public void creat(Group group) {
        groupMapper.insert(group);
    }

    @Override
    @Transactional
    public void deleteGroup(Integer groupId) {
        groupMapper.deleteById(groupId);
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        groupUserMapper.deleteByMap(map);
    }

    @Override
    public void deleteGroupUser(List<Integer> userIds) {
        Map<String, Object> map = new HashMap<>();
        for (Integer userId : userIds) {
            map.put("userId", userId);
        }
        groupUserMapper.deleteByMap(map);
    }

    @Override
    @Transactional
    public List<Group> getGroup(Integer userId) {
        QueryWrapper<GroupUser> groupUserQueryWrapper = new QueryWrapper<>();
        groupUserQueryWrapper.eq("userId", userId);
        List<GroupUser> groupUsers = groupUserMapper.selectList(groupUserQueryWrapper);
        List<Group> groups = new ArrayList<>();
        BeanUtil.copyProperties(groupUsers, groups);
        return groupMapper.selectBatchIds(groups);
    }

    @Override
    public void insert(Integer groupId, List<Integer> userIds) {
        for (Integer userId : userIds) {
            groupUserMapper.insert(new GroupUser(null, groupId, userId));
        }
    }
}
