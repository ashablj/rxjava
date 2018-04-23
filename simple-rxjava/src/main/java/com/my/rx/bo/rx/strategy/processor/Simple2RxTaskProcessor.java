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

import java.util.Collection;

import static java.lang.String.format;

public class Simple2RxTaskProcessor implements BoTaskProcessor<User> {

    private BoJobWorker<User> jobWorker;
    private boolean skipErrors = true;
    private Scheduler scheduler;

    @Override
    public void setJobWorker(BoJobWorker<User> jobWorker) {
        this.jobWorker = jobWorker;
    }

    @Override
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void setSkipErrors(boolean isSkip) {
        skipErrors = isSkip;
    }

    @Override
    public Collection<BoJobResult<User>> executeTask(BoTask task, Collection<User> data) {
        System.out.println("####################### ======> " + this.getClass().getSimpleName());
        Assert.assertNotNull("The Job executor can not be Null!", jobWorker);
        Assert.assertNotNull("The Task can not be Null!", task);
        Assert.assertNotNull("The Task data can not be Null!", data);

        scheduler.createWorker();

        Collection<BoJobResult<User>> lsJobResult = Lists.newArrayList();
        Observable.from(data)
    /*            .flatMap(
                        user ->
                                Observable.just(jobWorker.execute(user))
                )
    */            .observeOn(scheduler)
                .subscribeOn(scheduler)
                .subscribe(
                        u -> {
                            BoJobResult<User> result = jobWorker.execute(u);
                            System.out.println(format("[Thread: %s] returns result: %s", Thread.currentThread().getName(), result));
                            System.out.println("==============> Send event PROGRESS");
                            lsJobResult.add(result);
                        },
        /*)
                .subscribe(
                        res -> System.out.println("res: " + res),
*/
                        (e) ->
                                System.out.println(format("=======> Send event COMPLETED '%s' was FAILURE: %s.", task.attribute, e.getMessage())),

                        () ->
                                System.out.println(format("=======> Send event COMPLETED '%s' was SUCCESS.", task.attribute))
                );

        return lsJobResult;
    }
}