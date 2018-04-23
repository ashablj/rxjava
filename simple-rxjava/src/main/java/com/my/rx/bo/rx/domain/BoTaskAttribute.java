package com.my.rx.bo.rx.domain;

import com.google.common.base.MoreObjects;

import java.util.UUID;

/**
 */
public class BoTaskAttribute {

    public final String token;
    public final String name;
    public final String initiatorName;
    public final long operatorId;

    public BoTaskAttribute(String name, String initiatorName, long operatorId) {
        this.token = UUID.randomUUID().toString();
        this.name = name;
        this.initiatorName = initiatorName;
        this.operatorId = operatorId;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("token", token)
                .add("name", name)
                .add("initiatorName", initiatorName)
                .add("operatorId", operatorId)
                .toString();
    }
}