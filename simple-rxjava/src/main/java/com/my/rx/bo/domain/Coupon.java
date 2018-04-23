package com.my.rx.bo.domain;

import com.google.common.base.MoreObjects;

import java.util.Random;
import java.util.UUID;

public class Coupon {
    public long id;
    public String name;

    public Coupon generate() {
        Coupon user = new Coupon();
        user.id = new Random().nextInt(1000);
        user.name = UUID.randomUUID().toString();
        return user;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .toString();
    }
}