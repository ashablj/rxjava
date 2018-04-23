package com.my.rx.bo.rx.domain;

public class BoTask {

    public final BoTaskAttribute attribute;
    public final BoJob job;

    public BoTask(BoTaskAttribute attribute, BoJob job) {
        this.attribute = attribute;
        this.job = job;
    }
}