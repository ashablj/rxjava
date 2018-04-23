package com.my.rx.bo.rx.domain;

import java.util.Date;
import java.util.UUID;

/**
 */
public class BoJob {

    public final String token;
    public final String name;
    public BoJobContext context;
    public BoJobData data;

    public BoJob() {
        this.token = "job-" + new Date().getTime();
        this.name = UUID.randomUUID().toString();
    }
}
