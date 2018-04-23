package com.my.rx.bo.strategies;

import com.my.rx.bo.domain.User;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 */
public class GetUserExperience {

    public User get(long userId) {
        User user = new User();
        user.id = userId;
        user.experience = new Random().nextInt();
        System.out.println(getClass().getSimpleName() + " :" + user);

        try {
            TimeUnit.MILLISECONDS.sleep(220);
        } catch (InterruptedException ignored) {
        }
        return user;
    }
}

