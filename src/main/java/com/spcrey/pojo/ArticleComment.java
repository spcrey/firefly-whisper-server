package com.spcrey.pojo;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.groups.Default;
import lombok.Data;

@Data
public class ArticleComment {

    private Integer id;

    @NotNull(groups = Add.class)
    @Pattern(groups = Add.class, regexp = "^[\\s\\S]{1,512}$")
    private String content;

    private Integer userId;

    @NotNull(groups = Add.class)
    private Integer articleId;

    private LocalDateTime createTime;
    
    // extra param

    private String userNickname;

    private String userAvatarUrl;

    public interface Add extends Default {}
}
