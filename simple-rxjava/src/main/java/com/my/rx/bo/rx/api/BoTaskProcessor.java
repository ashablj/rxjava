package com.my.rx.bo.rx.api;

import com.my.rx.bo.rx.domain.BoJobResult;
import com.my.rx.bo.rx.domain.BoTask;
import rx.Scheduler;

import java.util.Collection;

/**
 */
public interface BoTaskProcessor<T> {

    void setJobWorker(BoJobWorker<T> jobWorker);

    void setScheduler(Scheduler scheduler);

    void setSkipErrors(boolean skip);

    Collection<BoJobResult<T>> executeTask(BoTask task, Collection<T> data);
}
