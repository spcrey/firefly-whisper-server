package com.spcrey.pojo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import lombok.Data;

@Data
public class UserFollow {

    public UserFollow(Integer followerUserId, Integer followedUserId) {
        this.followerUserId = followerUserId;
        this.followedUserId = followedUserId;
    }

    private Integer id;

    private Integer followerUserId;

    @NotNull(groups = Follow.class)
    private Integer followedUserId;

    public interface Follow extends Default {}
}
