package com.spcrey.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spcrey.pojo.Result;
import com.spcrey.pojo.User;
import com.spcrey.service.MessageService;
import com.spcrey.service.UserService;
import com.spcrey.utils.ThreadLocalUtil;
import com.spcrey.pojo.Message;
import com.spcrey.pojo.MessageUserList;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    UserService userService;

    @Autowired
    MessageService messageService;

    @PostMapping("sendText")
    public Result<?> sendText(@RequestBody @Validated(Message.SendText.class) Message message) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer sendingUserId = (Integer) map.get("id");
        Integer receivingUserId = message.getReceivingUserId();
        if (sendingUserId == receivingUserId) {
            return Result.error("can't send message to yourself");
        }
        User receivingUser = userService.findById(receivingUserId);
        if (receivingUser == null) {
            return Result.error("target user don't exit");
        }
        message.setSendingUserId(sendingUserId);
        message = messageService.create(message);

        if (message.getLastId() != null) {
            MessageUserList multiUserMessageList = (MessageUserList) list(message.getLastId()).getData();
            return Result.success(multiUserMessageList);
        } else {
            return Result.success();
        }
    }

    @PostMapping("sendImage")
    public Result<?> sendImage(@RequestBody @Validated(Message.SendImage.class) Message message) throws Exception {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer sendingUserId = (Integer) map.get("id");
        Integer receivingUserId = message.getReceivingUserId();
        if (sendingUserId == receivingUserId) {
            return Result.error("can't send message to yourself");
        }
        User receivingUser = userService.findById(receivingUserId);
        if (receivingUser == null) {
            return Result.error("target user don't exit");
        }
        message.setSendingUserId(sendingUserId);
        try {
            String url = userService.base64ToUrl(message.getImageUrl());
            message.setImageUrl(url);
            message = messageService.create(message);
            if (message.getLastId() != null) {
                MessageUserList multiUserMessageList = (MessageUserList) list(message.getLastId()).getData();
                return Result.success(multiUserMessageList);
            } else {
                return Result.success();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("list")
    public Result<?> list(@RequestParam(defaultValue = "0", required = false) Integer lastId) {
        MessageUserList multiUserMessageList = messageService.list(lastId);
        return Result.success(multiUserMessageList);
    }
}
