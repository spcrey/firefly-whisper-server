package com.spcrey.pojo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class MessageList {

    private Integer withUserId;

    private String userAvatarUrl;

    private String userNickname;

    private Integer lastMessageId;

    private List<Message> messages = new ArrayList<>();

    public void addMessage(Message message) {
        lastMessageId = message.getId();
        messages.add(message);
    }
}
