package com.my.rx.bo.strategies;

import static java.lang.String.format;

public class UpdateUserLevel {

    public boolean update(long userId, int level) {
        System.out.println(format("Service %s for userId: %d, level: %d was success", getClass().getSimpleName(), userId, level));
        return true;
    }
}
