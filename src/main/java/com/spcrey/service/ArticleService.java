package com.spcrey.service;

import java.util.List;

import com.spcrey.pojo.Article;
import com.spcrey.pojo.ArticleComment;
import com.spcrey.pojo.ArticleImage;
import com.spcrey.pojo.ArticleLike;
import com.spcrey.utils.PageBean;

public interface ArticleService {

    Article create(Article article);

    Article findById(Integer articleId);

    PageBean<Article> listByPageNumSizeToken(Integer pageNum, Integer pageSize, String token);

    void deleteById(Integer id);

    void addImage(ArticleImage articleImage);

    void addImageUrlsOnId(Integer articleId, List<String> articleImageUrls);

    void deleteImages(Integer id);

    void addLikeOnUserArticleId(Integer userId, Integer articleId);

    ArticleLike findLikeByUserArticleId(Integer userId, Integer articleId);
    
    void deleteLikeByUserArticleId(Integer userId, Integer articleId);

    void deleteLikesById(Integer id);

    void createComment(ArticleComment articleComment);

    List<ArticleComment> listCommentsById(Integer id);

    void deleteCommentsById(Integer id);
}
