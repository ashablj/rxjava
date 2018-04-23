package com.my.rx.bo.domain;

import com.google.common.base.MoreObjects;

import java.util.Random;

public class User {
    public long id;
    public int level;
    public double balance;
    public int experience;

    public User() {
    }

    public User(long id, int level, double balance, int experience) {
        this.id = id;
        this.level = level;
        this.experience = experience;
        this.balance = balance;
    }

    public User generate() {
        User user = new User();
        user.id = new Random().nextInt(1000);
        user.level = new Random().nextInt(20);
        user.balance = new Random().nextDouble();
        user.experience = new Random().nextInt(100);
        return user;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("level", level)
                .add("experience", experience)
                .add("balance", balance)
                .toString();
    }
}