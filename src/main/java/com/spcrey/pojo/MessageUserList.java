package com.spcrey.pojo;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lombok.Data;

@Data
public class MessageUserList {
    
    List<MessageList> userMessageLists;

    private Integer lastMessageId;

    public void addMessage(Message message, User withUser) {

        lastMessageId = message.getId();

        boolean isExitWithUser = false;
        for(int i=0; i<userMessageLists.size(); i++) {
            MessageList userMessageList = userMessageLists.get(i);
            if (userMessageList.getWithUserId() == withUser.getId()) {
                userMessageList.addMessage(message);
                isExitWithUser = true;
            } 
            userMessageLists.set(i, userMessageList);
        }
        if (isExitWithUser == false) {
            createWithUserAddMessage(withUser, message);
        }
    
    }

    void createWithUserAddMessage(User withUser, Message message) {
        MessageList userMessageList = new MessageList();
        userMessageList.addMessage(message);
        userMessageList.setUserNickname(withUser.getNickname());
        userMessageList.setUserAvatarUrl(withUser.getAvatarUrl());
        userMessageList.setWithUserId(withUser.getId());
        userMessageLists.add(userMessageList);
    }

    public void sort() {
        Collections.sort(userMessageLists, Comparator.comparingInt(MessageList::getLastMessageId).reversed());
    }
}
