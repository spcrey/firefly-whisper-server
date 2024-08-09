package com.spcrey.service;

import java.util.List;

import com.spcrey.pojo.User;
import com.spcrey.pojo.UserFollow;

public interface UserService {

    public void addByPhoneNumberPassword(String phoneNumber, String password);

    public User createByPhoneNumber(String phoneNumber);

    public User findById(Integer id);

    public User findByPhoneNumber(String phoneNumber);

    public String getToken(User user);

    public User suppleFollowByToken(User user, String token);

    public void update(User user);

    public void updatePassword(User user, String password);

    public boolean checkPassword(User user, String password);

    public void updateAvatar(String avatarUrl);

    public void addFollow(UserFollow userFollow);

    public UserFollow findFollow(UserFollow userFollow);

    public List<User> listFolloweds();

    public List<User> listFollowers();

    public void deleteFollow(UserFollow userFollow);

    public void deleteToken(String token);

    public String base64ToUrl(String string) throws Exception;
}
