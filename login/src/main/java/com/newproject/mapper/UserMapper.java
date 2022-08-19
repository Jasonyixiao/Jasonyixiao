package com.newproject.mapper;

import com.newproject.entity.User;

import java.util.List;

public interface UserMapper {

    List<User> getAllUser();

    User getUserByUsername(String username);

    int addUser(User user);

    int deleteUser(String username);

    int updateUserEmail(User user);

    int updateUserPassword(User user);

    int updateUserName(User user);

    int updateDOB(User user);

    int updateJwt(User user);

}