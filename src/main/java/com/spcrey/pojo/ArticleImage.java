package com.spcrey.pojo;

import lombok.Data;

@Data
public class ArticleImage {
    
    private Integer id;

    private Integer articleId;

    private String imageUrl;
}
