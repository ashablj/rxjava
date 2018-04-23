package com.my.rx.bo.rx.domain;

import com.google.common.base.MoreObjects;
import com.my.rx.bo.rx.enums.RxJobStatus;

public class BoJobResult<T> {

    public RxJobStatus status = RxJobStatus.SUCCESS;
    public String errorMessage;
    public final T data;

    public BoJobResult(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("status", status)
                .add("errorMessage", errorMessage)
                .add("data", data)
                .toString();
    }
}
