package com.my.rx.bo.rx.domain;

public class BoTaskReport<R> {

    public final BoTaskAttribute attribute;
    public final R result;
    public String status = "COMPLETED";

    public BoTaskReport(BoTaskAttribute attribute, R result) {
        this.attribute = attribute;
        this.result = result;
    }
}