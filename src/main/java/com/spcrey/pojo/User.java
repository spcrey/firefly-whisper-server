package com.spcrey.pojo;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.groups.Default;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
public class User {

    @NotNull(groups = InfoOther.class)
    private Integer id;

    private String phoneNumber;

    @JsonIgnore
    private String password;

    @Pattern(groups = Update.class, regexp = "^[\\s\\S]{3,8}$")
    private String nickname;

    @Email(groups = Update.class, regexp = "^\\S{1,128}$")
    private String email;

    @NotNull(groups = UpdateAvatar.class)
    private String avatarUrl;

    @Pattern(groups = Update.class, regexp = "^[\\s\\S]{1,32}$")
    private String personalSignature;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Boolean isFollowed;

    private Boolean isFollower;
    
    public interface Update extends Default {}
    
    public interface InfoOther extends Default {}

    public interface UpdateAvatar extends Default {}

    public void hidePartInfo() {
        phoneNumber = null;
        createTime = null;
        updateTime = null;
    }
}
