package com.spcrey.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.spcrey.pojo.User;
import com.spcrey.pojo.UserFollow;

@Mapper
public interface UserMapper {

    // user

    @Insert("insert into user(phone_number, password, nickname, avatar_url, create_time, update_time) values(#{phoneNumber}, #{password}, #{nickname}, #{avatarUrl}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void create(User user);

    @Select("select * from user where id=#{id}")
    User findById(Integer id);

    @Select("select * from user where phone_number=#{phoneNumber}")
    User findByPhoneNumber(String phoneNumber);

    @Update("update user set nickname=#{nickname}, email=#{email}, personal_signature=#{personalSignature}, update_time=#{updateTime} where id=#{id}")
    void update(User user);

    @Update("update user set password=#{password}, update_time=#{updateTime} where id=#{id}")
    void updatePassword(User user);

    @Update("update user set avatar_url=#{avatarUrl}, update_time=#{updateTime} where id=#{id}")
    void updateAvatar(User user);

    // follow

    @Insert("insert into user_follow(follower_user_id, followed_user_id) values(#{followerUserId}, #{followedUserId})")
    void addFollow(UserFollow follow);

    @Select("select * from user_follow where follower_user_id=#{followerUserId} and followed_user_id=#{followedUserId}")
    UserFollow findFollow(UserFollow follow);

    @Select("select * from user_follow where follower_user_id=#{followerUserId}")
    List<UserFollow> listFollowsByFollowerId(Integer followerId);

    @Select("select * from user_follow where followed_user_id=#{followedUserId}")
    List<UserFollow> listFollowsByFollowedId(Integer followedId);

    @Delete("delete from user_follow where follower_user_id=#{followerUserId} and followed_user_id=#{followedUserId}")
    void deleteFollow(UserFollow follow);
}
