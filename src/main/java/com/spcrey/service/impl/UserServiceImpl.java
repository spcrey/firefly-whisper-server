package com.spcrey.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.spcrey.mapper.UserMapper;
import com.spcrey.pojo.User;
import com.spcrey.pojo.UserFollow;
import com.spcrey.service.UserService;
import com.spcrey.utils.AliyunOssUtil;
import com.spcrey.utils.JwtUtil;
import com.spcrey.utils.MD5Util;
import com.spcrey.utils.ThreadLocalUtil;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    public User findByPhoneNumber(String phoneNumber) {
        return userMapper.findByPhoneNumber(phoneNumber);
    }

    public User createByPhoneNumberWithoutMapper(String phoneNumber) {
        User user = new User();
        user.setPhoneNumber(phoneNumber);
        String nickname = "user" + phoneNumber.substring(7);
        String avatarUrl = "https://firefly-whisper.oss-cn-beijing.aliyuncs.com/new_user.jpg";
        user.setNickname(nickname);
        user.setAvatarUrl(avatarUrl);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        return user;
    }

    @Override
    public User createByPhoneNumber(String phoneNumber) {
        User user = createByPhoneNumberWithoutMapper(phoneNumber);
        userMapper.create(user);
        return user;
    }

    @Override
    public void addByPhoneNumberPassword(String phoneNumber, String password) {
        User user = createByPhoneNumberWithoutMapper(phoneNumber);
        password = MD5Util.StringToMD5(password);
        user.setPassword(password);
        userMapper.create(user);
    }

    @Override
    public String getToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("phoneNumber", user.getPhoneNumber());
        String token = JwtUtil.genToken(claims);
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.set(token, token, 7, TimeUnit.DAYS);
        return token;
    }

    @Override
    public void update(User user) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer id = (Integer) map.get("id");
        user.setId(id);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user);
    }

    @Override
    public void updatePassword(User user, String password) {
        password = MD5Util.StringToMD5(password);
        user.setPassword(password);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updatePassword(user);
    }

    @Override
    public void updateAvatar(String avatar) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        User user = new User();
        user.setId(userId);
        user.setAvatarUrl(avatar);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateAvatar(user);
    }

    public List<User> listFollowedsOrFollowers(Boolean isFollower) {
        Map<String, Object> map = ThreadLocalUtil.get();
        String phoneNumber = (String) map.get("phoneNumber");
        User user = findByPhoneNumber(phoneNumber);
        Integer userId = user.getId();
        List<UserFollow> userFollows;
        if (isFollower) {
            userFollows = userMapper.listFollowsByFollowedId(userId);
        } else {
            userFollows = userMapper.listFollowsByFollowerId(userId);
        }
        List<User> followeds = new ArrayList<>();
        for(UserFollow userFollow: userFollows) {
            User followed;
            if (isFollower) {
                followed = userMapper.findById(userFollow.getFollowerUserId());
            } else {
                followed = userMapper.findById(userFollow.getFollowedUserId());
            }
            followed.hidePartInfo();
            followeds.add(followed);
        }
        return followeds;
    }

    @Override
    public List<User> listFollowers() {
        return listFollowedsOrFollowers(true);
    }

    @Override
    public List<User> listFolloweds() {
        return listFollowedsOrFollowers(false);
    }

    @Override
    public User findById(Integer id) {
        return userMapper.findById(id);
    }

    @Override
    public void deleteToken(String token) {
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.getOperations().delete(token);
    }

    @Override
    public UserFollow findFollow(UserFollow userFollow) {
        return userMapper.findFollow(userFollow);
    }

    @Override
    public void addFollow(UserFollow userFollow) {
        userMapper.addFollow(userFollow);
    }

    @Override
    public void deleteFollow(UserFollow userFollow) {
        userMapper.deleteFollow(userFollow);
    }

    @Override
    public User suppleFollowByToken(User user, String token) {
        Map<String, Object> claims = JwtUtil.parseToken(token);
        Integer userId = (Integer) claims.get("id");
        UserFollow userFollow = findFollow(new UserFollow(userId, user.getId()));
        if (userFollow != null) {
            user.setIsFollowed(true);
        } else {
            user.setIsFollowed(false);
        }
        userFollow = findFollow(new UserFollow(user.getId(), userId));
        if (userFollow != null) {
            user.setIsFollower(true);
        } else {
            user.setIsFollower(false);
        }
        return user;
    }

    @Override
    public boolean checkPassword(User user, String inputPassword) {
        inputPassword = MD5Util.StringToMD5(inputPassword);
        String correctPassword = user.getPassword();
        if (correctPassword == null || !correctPassword.equals(inputPassword)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String base64ToUrl(String fileBase64) throws Exception {
        byte[] decodedBytes = Base64.getDecoder().decode(fileBase64);
        InputStream inputStream = new ByteArrayInputStream(decodedBytes);        
        String filename = UUID.randomUUID().toString() + ".jpg";
        String url = AliyunOssUtil.uploadFile(filename, inputStream);
        return url;
    }
}
