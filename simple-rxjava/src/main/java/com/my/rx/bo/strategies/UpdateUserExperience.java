package com.my.rx.bo.strategies;

import static java.lang.String.format;

public class UpdateUserExperience {

    public boolean update(long userId, double balance) {
        System.out.println(format("Service %s for userId: %d, level: %s was success", getClass().getSimpleName(), userId, balance));
        return true;
    }
}
