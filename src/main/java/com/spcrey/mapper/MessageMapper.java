package com.spcrey.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import com.spcrey.pojo.Message;

@Mapper
public interface MessageMapper {
    @Insert("insert into message(image_url, text_content, sending_user_id, receiving_user_id, create_time) values(#{imageUrl}, #{textContent}, #{sendingUserId}, #{receivingUserId}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void create(Message message);

    @Select("select * from message where sending_user_id=#{userId} and id>#{lastId}")
    List<Message> listBySendingUserId(Integer userId, Integer lastId);

    @Select("select * from message where receiving_user_id=#{userId} and id>#{lastId}")
    List<Message> listByReceivingUserId(Integer userId, Integer lastId);
}
