package com.spcrey.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spcrey.mapper.MessageMapper;
import com.spcrey.pojo.Message;
import com.spcrey.pojo.MessageUserList;
import com.spcrey.pojo.User;
import com.spcrey.pojo.MessageList;
import com.spcrey.service.MessageService;
import com.spcrey.service.UserService;
import com.spcrey.utils.ThreadLocalUtil;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    MessageMapper messageMapper;

    @Autowired
    UserService userService;

    @Override
    public Message create(Message message) {
        message.setCreateTime(LocalDateTime.now());
        messageMapper.create(message);
        return message;
    }

    private List<Message> mergeSendingReceivingMessages(List<Message> sendingMessages, List<Message> receivingMessages) {
        List<Message> messages = new ArrayList<>();
        for (int i=0; i<sendingMessages.size(); i++) {
            Message message = sendingMessages.get(i);
            message.setIsSendingUser(true);
            message.setWithUserId(message.getReceivingUserId());
            sendingMessages.set(i, message);
        }
        for (int i=0; i<receivingMessages.size(); i++) {
            Message message = receivingMessages.get(i);
            message.setIsSendingUser(false);
            message.setWithUserId(message.getSendingUserId());
            receivingMessages.set(i, message);
        }
        messages.addAll(sendingMessages);
        messages.addAll(receivingMessages);
        Collections.sort(messages, Comparator.comparingInt(Message::getId));
        return messages;
    }

    @Override
    public MessageUserList list(Integer lastId) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer currentUserId = (Integer) map.get("id");

        List<Message> sendingMessages = messageMapper.listBySendingUserId(currentUserId, lastId);
        List<Message> receivingMessages = messageMapper.listByReceivingUserId(currentUserId, lastId);
        List<Message> messages = mergeSendingReceivingMessages(sendingMessages, receivingMessages);

        List<MessageList> userMessageLists = new ArrayList<>();
        MessageUserList multiUserMessageList = new MessageUserList();
        multiUserMessageList.setUserMessageLists(userMessageLists);

        for(Message message: messages) {
            User withUser = userService.findById(message.getWithUserId());
            message.setWithUserId(null);
            multiUserMessageList.addMessage(message, withUser);
        }
        multiUserMessageList.sort();
        
        return multiUserMessageList;
    }
}
