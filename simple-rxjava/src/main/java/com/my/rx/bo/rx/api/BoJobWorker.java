package com.my.rx.bo.rx.api;

import com.my.rx.bo.rx.domain.BoJobResult;

/**
 */
public interface BoJobWorker<T> {

    BoJobResult<T> execute(T data);
}
