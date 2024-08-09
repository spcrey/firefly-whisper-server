package com.spcrey.pojo;

import java.time.LocalDateTime;
import java.util.List;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.groups.Default;
import lombok.Data;

@Data
public class Article {

    @NotNull(groups = {Delete.class, Like.class, Unlike.class})
    private Integer id;

    @Pattern(groups = Add.class, regexp = "^[\\s\\S]{1,512}$")
    private String content;
    
    private Integer userId;

    private LocalDateTime createTime;

    // extra param

    private List<String> imageUrls;

    private String userNickname;

    private String userAvatarUrl;

    private Integer likeCount;

    private Boolean likeStatus;

    private Integer commentCount;

    public interface Add extends Default {}

    public interface Delete extends Default {}

    public interface Like extends Default {}

    public interface Unlike extends Default {}
}
