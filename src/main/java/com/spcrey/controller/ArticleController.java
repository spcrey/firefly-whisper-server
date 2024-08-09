package com.spcrey.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spcrey.pojo.Article;
import com.spcrey.pojo.ArticleComment;
import com.spcrey.pojo.ArticleLike;
import com.spcrey.pojo.Result;
import com.spcrey.service.ArticleService;
import com.spcrey.service.UserService;
import com.spcrey.utils.PageBean;
import com.spcrey.utils.ThreadLocalUtil;


@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    ArticleService articleService;

    @Autowired
    UserService userService;

    @GetMapping
    public Result<String> article() {
        return Result.success("This is article page!");
    }

    @GetMapping("list")
    public Result<PageBean<Article>> list(Integer pageNum, Integer pageSize, @RequestHeader(name = "Authorization", required = false) String token) {
        PageBean<Article> pa = articleService.listByPageNumSizeToken(pageNum, pageSize, token);
        return Result.success(pa);
    }

    @PostMapping("add")
    public Result<?> add(@RequestBody @Validated(Article.Add.class) Article article) throws Exception {
        article = articleService.create(article);
        List<String> articleImageUrls = article.getImageUrls();
        for (int i=0; i<articleImageUrls.size(); i++) {
            String fileBase64 = articleImageUrls.get(i);
            try {
                String url = userService.base64ToUrl(fileBase64);
                articleImageUrls.set(i, url);
            } catch (Exception e) {
                e.printStackTrace();
                return Result.error(e.getMessage());
            }
        }
        articleService.addImageUrlsOnId(article.getId(), articleImageUrls); 
        return Result.success();
    }

    @PostMapping("delete")
    public Result<?> delete(@RequestBody @Validated(Article.Delete.class) Article article) {
        Integer id = article.getId();
        article = articleService.findById(id);
        if (article == null) {
            return Result.error("article does not exist");
        }
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        if (userId != article.getUserId()) {
            return Result.error("not your article");
        }
        articleService.deleteById(id);
        return Result.success();
    }

    @PostMapping("like")
    public Result<?> like(@RequestBody @Validated(Article.Like.class) Article article) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        Integer articleId = article.getId();
        ArticleLike articleLike = articleService.findLikeByUserArticleId(userId, articleId);
        if (articleLike != null) {
            return Result.error("already like the article");
        }
        articleService.addLikeOnUserArticleId(userId, articleId);
        return Result.success();
    }

    @PostMapping("unlike")
    public Result<?> unlike(@RequestBody @Validated(Article.Unlike.class) Article article) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        Integer articleId = article.getId();
        ArticleLike articleLike = articleService.findLikeByUserArticleId(userId, articleId);
        if (articleLike == null) {
            return Result.error("already unlike the article");
        }
        articleService.deleteLikeByUserArticleId(userId, articleId);
        return Result.success();
    }

    @PostMapping("comment")
    public Result<?> comment(@RequestBody @Validated(ArticleComment.Add.class) ArticleComment articleComment) {
        Article article = articleService.findById(articleComment.getArticleId());
        if (article == null) {
            return Result.error("article does not exist");
        }
        articleService.createComment(articleComment);
        return Result.success();
    }
    
    @GetMapping("listComments")
    public Result<?> listComment(Integer articleId) {
        Article article = articleService.findById(articleId);
        if (article == null) {
            return Result.error("article does not exist");
        }
        List<ArticleComment> articleComment = articleService.listCommentsById(article.getId());
        return Result.success(articleComment);
    }
}
