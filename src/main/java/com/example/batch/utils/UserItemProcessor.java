package com.example.batch.utils;

import com.example.batch.domain.User;
import org.springframework.batch.item.ItemProcessor;

public class UserItemProcessor implements ItemProcessor<User, User> {

    @Override
    public User process(User user) {

       user.setComments("--COMMENTS ADDED From PROCESSOR");
       return user;
    }
}
