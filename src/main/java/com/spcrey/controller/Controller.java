package com.spcrey.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import com.spcrey.pojo.Result;
import com.spcrey.service.UserService;

@RestController
@RequestMapping
public class Controller {

    @Autowired
    UserService userService;

    @GetMapping
    public Result<String> firefly() {
        return Result.success("Hello World, Firefly!");
    }
}
