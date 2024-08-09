package com.spcrey.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import com.spcrey.pojo.Article;
import com.spcrey.pojo.ArticleComment;
import com.spcrey.pojo.ArticleImage;
import com.spcrey.pojo.ArticleLike;

@Mapper
public interface ArticleMapper {

    // article

    @Insert("insert into article(content, user_id, create_time) values(#{content}, #{userId}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void create(Article article);

    @Select("select * from article where id=#{id}")
    Article findById(Integer id);

    @Select("select * from article order by id desc")
    List<Article> list();

    @Delete("delete from article where id=#{id}")
    void deleteById(Integer id);

    // image

    @Insert("insert into article_image(article_id, image_url) values(#{articleId}, #{imageUrl})")
    void addImage(ArticleImage image);

    @Select("select * from article_image where article_id=#{id}")
    List<ArticleImage> findImagesById(Integer id);

    @Delete("delete from article_image where article_id=#{id}")
    void deleteImagesById(Integer id);

    // like

    @Insert("insert into article_like(user_id, article_id) values(#{userId}, #{articleId})")
    void addLike(ArticleLike like);

    @Select("select * from article_like where user_id=#{userId} and article_id=#{articleId}")
    ArticleLike findLike(ArticleLike like);

    @Select("select * from article_like where article_id=#{id}")
    List<ArticleLike> listLikesById(Integer id);

    @Delete("delete from article_like where user_id=#{userId} and article_id=#{articleId}")
    void deleteLike(ArticleLike like);

    @Delete("delete from article_like where article_id=#{id}")
    void deleteLikesById(Integer id);

    // comment

    @Insert("insert into article_comment(content, user_id, article_id, create_time) values(#{content}, #{userId}, #{articleId}, #{createTime})")
    void addComment(ArticleComment comment);

    @Select("select * from article_comment where article_id=#{id} order by id desc")
    List<ArticleComment> listCommentsById(Integer id);

    @Delete("delete from article_comment where article_id=#{id}")
    void deleteCommentsById(Integer id);
}
