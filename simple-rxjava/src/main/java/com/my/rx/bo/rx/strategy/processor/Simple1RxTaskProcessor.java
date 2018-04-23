package com.my.rx.bo.rx.strategy.processor;

import com.google.common.collect.Lists;
import com.my.rx.bo.domain.User;
import com.my.rx.bo.rx.api.BoJobWorker;
import com.my.rx.bo.rx.api.BoTaskProcessor;
import com.my.rx.bo.rx.domain.BoJobResult;
import com.my.rx.bo.rx.domain.BoTask;
import org.junit.Assert;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import java.util.Collection;
import java.util.concurrent.Executors;

import static java.lang.String.format;

public class Simple1RxTaskProcessor implements BoTaskProcessor<User> {

    private int workersPoolSize = 1;
    private BoJobWorker<User> jobWorker;
    private boolean skipErrors = true;

    @Override
    public void setJobWorker(BoJobWorker<User> jobWorker) {
        this.jobWorker = jobWorker;
    }

    @Override
    public void setScheduler(Scheduler scheduler) {

    }

    public void setWorkersPoolSize(int workersPoolSize) {
        this.workersPoolSize = workersPoolSize;
    }

    @Override
    public void setSkipErrors(boolean isSkip) {
        skipErrors = isSkip;
    }

    @Override
    public Collection<BoJobResult<User>> executeTask(BoTask task, Collection<User> data) {
        Assert.assertNotNull("The Job executor can not be Null!", jobWorker);
        Assert.assertNotNull("The Task can not be Null!", task);
        Assert.assertNotNull("The Task data can not be Null!", data);

//        Scheduler procCoreExecutor = Schedulers.from(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
//        Scheduler singleExecutor = Schedulers.from(Executors.newSingleThreadExecutor());
//        Scheduler ioExecutor = Schedulers.io();

        Collection<BoJobResult<User>> lsJobResult = Lists.newArrayList();
        Scheduler scheduler = createScheduler();

        Observable.from(data)
//                .observeOn(createScheduler())
//                .subscribeOn(createScheduler())
                .subscribe(
                        user ->
                                Observable.just(jobWorker.execute(user))
                                        .observeOn(scheduler)
//                                        .subscribeOn(scheduler)
                                        .subscribe(
                                                result -> {
                                                    System.out.println(format("[Thread: %s] returns result: %s", Thread.currentThread().getName(), result));
                                                    System.out.println("==============> Send event PROGRESS");
                                                    lsJobResult.add(result);
                                                },

                                                (e) -> {
                                                    System.out.println("Logging error: " + e);
                                                },

                                                () -> System.out.println("-------> Logging: next part was completed.")
                                        ),

                        (e) ->
                                System.out.println(format("=======> Send event COMPLETED '%s' was FAILURE: %s.", task.attribute, e.getMessage())),

                        () ->
                                System.out.println(format("=======> Send event COMPLETED '%s' was SUCCESS.", task.attribute))
                );

        return lsJobResult;
    }

    private Scheduler createScheduler() {
        return workersPoolSize > 1
                ? Schedulers.from(Executors.newFixedThreadPool(workersPoolSize))
                : Schedulers.from(Executors.newSingleThreadExecutor());
    }
}