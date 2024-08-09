package com.spcrey.service;

import com.spcrey.pojo.Message;
import com.spcrey.pojo.MessageUserList;

public interface MessageService {

    Message create(Message message);

    MessageUserList list(Integer lastId);
    
}
