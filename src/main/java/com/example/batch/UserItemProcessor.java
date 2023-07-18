package com.example.batch;

import com.example.batch.domain.User;
import org.springframework.batch.item.ItemProcessor;

public class UserItemProcessor implements ItemProcessor<User, User> {

    @Override
    public User process(User item) throws Exception {
        System.out.println(item);

        return User.builder().id(item.getId())
                .fname("PRAJAPATI_SURESH")

        .build();
    }
}
