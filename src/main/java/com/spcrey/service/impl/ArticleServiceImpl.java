package com.spcrey.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.spcrey.mapper.ArticleMapper;
import com.spcrey.mapper.UserMapper;
import com.spcrey.pojo.Article;
import com.spcrey.pojo.ArticleComment;
import com.spcrey.pojo.ArticleImage;
import com.spcrey.pojo.ArticleLike;
import com.spcrey.pojo.User;
import com.spcrey.service.ArticleService;
import com.spcrey.service.UserService;
import com.spcrey.utils.JwtUtil;
import com.spcrey.utils.PageBean;
import com.spcrey.utils.ThreadLocalUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    ArticleMapper articleMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    UserService userService;

    public PageBean<Article> listByPageNumSizeToken(Integer pageNum, Integer pageSize, String token) {
        PageBean<Article> pb = new PageBean<>();
        PageHelper.startPage(pageNum, pageSize);
        List<Article> articles = articleMapper.list();
        try (Page<Article> p = (Page<Article>) articles) {
            pb.setTotal(p.getTotal());
            pb.setItems(p.getResult());
        }
        articles = pb.getItems();
        for(int i = 0; i < articles.size(); i++) {
            Article article = articles.get(i);
            List<ArticleImage> articleImages = articleMapper.findImagesById(article.getId());
            // add imageUrls
            List<String> imageUrls = new ArrayList<>();
            for(ArticleImage articleImage: articleImages) {
                imageUrls.add(articleImage.getImageUrl());
            }
            article.setImageUrls(imageUrls);
            // add likes
            List<ArticleLike> likes = articleMapper.listLikesById(article.getId());
            article.setLikeCount(likes.size());
            // add comments
            List<ArticleComment> comments = articleMapper.listCommentsById(article.getId());
            article.setCommentCount(comments.size());
            // add likeStatus
            if (token != null){
                Map<String, Object> claims = JwtUtil.parseToken(token);
                Integer userId = (Integer) claims.get("id");
                ArticleLike like = new ArticleLike();
                like.setArticleId(article.getId());
                like.setUserId(userId);
                like = articleMapper.findLike(like);
                Boolean likeStatus = false;
                if (like != null) {
                    likeStatus = true;
                }
                article.setLikeStatus(likeStatus);
            }
            // add userNickname
            User user = userMapper.findById(article.getUserId());
            article.setUserNickname(user.getNickname());
            // add userAvatarUrl
            article.setUserAvatarUrl(user.getAvatarUrl());
            articles.set(i, article);
        }
        pb.setItems(articles);
        return pb;
    }

    @Override
    public Article create(Article article) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        article.setCreateTime(LocalDateTime.now());
        article.setUserId(userId);
        articleMapper.create(article);
        return article;
    }

    @Override
    public void deleteById(Integer id) {
        deleteImages(id);
        deleteCommentsById(id);
        deleteLikesById(id);
        articleMapper.deleteById(id);
    }

    @Override
    public void addImage(ArticleImage articleImage) {
        articleMapper.addImage(articleImage);
    }

    @Override
    public ArticleLike findLikeByUserArticleId(Integer userId, Integer articleId) {
        ArticleLike like = new ArticleLike();
        like.setArticleId(articleId);
        like.setUserId(userId);
        return articleMapper.findLike(like);
    }

    @Override
    public void addLikeOnUserArticleId(Integer userId, Integer articleId) {
        ArticleLike like = new ArticleLike();
        like.setArticleId(articleId);
        like.setUserId(userId);
        articleMapper.addLike(like);
    }

    @Override
    public void deleteLikeByUserArticleId(Integer userId, Integer articleId) {
        ArticleLike like = new ArticleLike();
        like.setArticleId(articleId);
        like.setUserId(userId);
        articleMapper.deleteLike(like);
    }

    @Override
    public void addImageUrlsOnId(Integer articleId, List<String> articleImageUrls) { 
        for(String articleImageUrl: articleImageUrls) {
            ArticleImage articleImage = new ArticleImage();
            articleImage.setArticleId(articleId);
            articleImage.setImageUrl(articleImageUrl);
            addImage(articleImage);
        }
    }

    @Override
    public Article findById(Integer articleId) {
        return articleMapper.findById(articleId);
    }

    @Override
    public void createComment(ArticleComment articleComment) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        articleComment.setUserId(userId);
        articleComment.setCreateTime(LocalDateTime.now());
        articleMapper.addComment(articleComment);
    }

    @Override
    public List<ArticleComment> listCommentsById(Integer id) {
        List<ArticleComment> comments = articleMapper.listCommentsById(id);
        for(int i=0; i<comments.size(); i++){
            ArticleComment comment = comments.get(i);
            User user = userService.findById(comment.getUserId());
            comment.setUserNickname(user.getNickname());
            comment.setUserAvatarUrl(user.getAvatarUrl());
            comments.set(i, comment);
        }
        return comments;
    }

    @Override
    public void deleteCommentsById(Integer id) {
        articleMapper.deleteCommentsById(id);
    }

    @Override
    public void deleteLikesById(Integer id) {
        articleMapper.deleteLikesById(id);
    }

    @Override
    public void deleteImages(Integer id) {
        articleMapper.deleteImagesById(id);
    }
}
