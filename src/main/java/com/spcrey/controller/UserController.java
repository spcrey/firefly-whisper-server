package com.spcrey.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spcrey.form.PhoneCodePasswordForm;
import com.spcrey.pojo.Result;
import com.spcrey.pojo.User;
import com.spcrey.pojo.UserFollow;
import com.spcrey.service.SmsService;
import com.spcrey.service.UserService;
import com.spcrey.utils.ThreadLocalUtil;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    SmsService smsService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @GetMapping
    public Result<String> user() {
        return Result.success("This is user page!");
    }

    @PostMapping("register")
    public Result<?> register(@RequestBody @Validated(PhoneCodePasswordForm.Register.class) PhoneCodePasswordForm form) {
        if (!form.getPassword().equals(form.getRePassword())) {
            return Result.error("passwords do not match");
        } 
        String phoneNumber = form.getPhoneNumber();
        User user = userService.findByPhoneNumber(phoneNumber);
        if (user != null) {
            return Result.error("this phone number is already registered");
        } 
        String password = form.getPassword();
        userService.addByPhoneNumberPassword(phoneNumber, password);
        return Result.success();
    }

    @PostMapping("/sendSms")
    public Result<?> sendSms(@RequestBody @Validated(PhoneCodePasswordForm.SendSms.class) PhoneCodePasswordForm form) {
        String phoneNumber = form.getPhoneNumber();
        String code = smsService.generateCode();
        smsService.savePhoneNumberCodeToCache(phoneNumber, code);
        System.out.println("code: " + code);
        smsService.sendCodeToPhoneNumber(phoneNumber, code);
        return Result.success();
    }

    @PostMapping("/loginByCode")
    public Result<String> loginByCode(@RequestBody @Validated(PhoneCodePasswordForm.LoginByCode.class) PhoneCodePasswordForm form) {
        String phoneNumber = form.getPhoneNumber();
        String code = form.getCode();
        if (!code.equals(smsService.getPhoneNumberCodeFromCache(phoneNumber))) {
            return Result.error("incorrect verification code");
        } 
        User user = userService.findByPhoneNumber(phoneNumber);
        if (user == null) {
            user = userService.createByPhoneNumber(phoneNumber);
        } 
        smsService.deletePhoneNumberCodeFromCache(phoneNumber);
        String token = userService.getToken(user);
        return Result.success(token);
    }

    @PostMapping("/loginByPassword")
    public Result<String> loginByPassword(@RequestBody @Validated(PhoneCodePasswordForm.LoginByPassword.class) PhoneCodePasswordForm form) {
        String phoneNumber = form.getPhoneNumber();
        String password = form.getPassword();
        User user = userService.findByPhoneNumber(phoneNumber);
        if (user == null) {
            return Result.error("user does not exist");
        } 
        if (!userService.checkPassword(user, password)) {
            return Result.error("incorrect password");
        }
        String token = userService.getToken(user);
        return Result.success(token);
    }

    @GetMapping("/info")
    public Result<User> info() {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer id = (Integer) map.get("id");
        User user = userService.findById(id);
        return Result.success(user);
    }

    @GetMapping("/infoOther")
    public Result<User> infoOther(Integer userId, @RequestHeader(name = "Authorization", required = false) String token) {
        User user = userService.findById(userId);
        if (user == null) {
            return Result.error("user does not exist");
        }
        user.hidePartInfo();
        if (token != null)
            user = userService.suppleFollowByToken(user, token);
        return Result.success(user);
    }

    @PostMapping("/updateAvatar")
    public Result<?> updateAvatar(@RequestBody @Validated(User.UpdateAvatar.class) User user) throws Exception {
        try {
            String url = userService.base64ToUrl(user.getAvatarUrl());
            userService.updateAvatar(url);
            return Result.success();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/updatePassword")
    public Result<?> updatePassword(@RequestBody @Validated(PhoneCodePasswordForm.UpdatePassword.class) PhoneCodePasswordForm form) {
        String phoneNumber = form.getPhoneNumber();
        String code = form.getCode();
        String password = form.getPassword();
        if (!code.equals(smsService.getPhoneNumberCodeFromCache(phoneNumber))) {
            return Result.error("incorrect verification code");
        } 
        User user = userService.findByPhoneNumber(phoneNumber);
        if (user == null) {
            return Result.error("user does not exist");
        }
        smsService.deletePhoneNumberCodeFromCache(phoneNumber);
        userService.updatePassword(user, password);
        return Result.success();
    }

    @PostMapping("/update")
    public Result<User> update(@RequestBody @Validated(User.Update.class) User user) {
        userService.update(user);
        return Result.success();
    }

    @PostMapping("/follow")
    public Result<User> follow(@RequestBody @Validated(UserFollow.Follow.class) UserFollow userFollow) {
        Integer followedUserId = userFollow.getFollowedUserId();
        if (userService.findById(followedUserId) == null) {
            return Result.error("user to follow does not exist");
        }
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer followerUserId = (Integer) map.get("id");
        if (followerUserId == followedUserId) {
            return Result.error("cannot follow yourself");
        }
        userFollow = userService.findFollow(new UserFollow(followerUserId, followedUserId));
        if (userFollow != null) {
            return Result.error("already following this user");
        }
        userService.addFollow(new UserFollow(followerUserId, followedUserId));
        return Result.success();
    }

    @PostMapping("/unfollow")
    public Result<User> unfollow(@RequestBody @Validated(UserFollow.Follow.class) UserFollow userFollow) {
        Integer followedUserId = userFollow.getFollowedUserId();
        if (userService.findById(followedUserId) == null) {
            return Result.error("user to follow does not exist");
        }
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer followerUserId = (Integer) map.get("id");
        if (followerUserId == followedUserId) {
            return Result.error("cannot unfollow yourself");
        }
        userFollow = userService.findFollow(new UserFollow(followerUserId, followedUserId));
        if (userFollow == null) {
            return Result.error("already unfollowing this user");
        }
        userService.deleteFollow(new UserFollow(followerUserId, followedUserId));
        return Result.success();
    }

    @GetMapping("/listFolloweds")
    public Result<List<User>> getFolloweds() {
        List<User> followeds = userService.listFolloweds();
        return Result.success(followeds);
    }

    @GetMapping("/listFollowers")
    public Result<List<User>> getFollowers() {
        List<User> followers = userService.listFollowers();
        return Result.success(followers);
    }

    @PostMapping("/logout")
    public Result<?> logout(@RequestHeader(name = "Authorization") String token) {
        userService.deleteToken(token);
        return Result.success();
    }
}
