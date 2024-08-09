package com.spcrey.pojo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import lombok.Data;

@Data
public class ArticleLike {
    
    private Integer id;

    private Integer userId;

    @NotNull(groups = Add.class)
    private Integer articleId;

    public interface Add extends Default {}
}
