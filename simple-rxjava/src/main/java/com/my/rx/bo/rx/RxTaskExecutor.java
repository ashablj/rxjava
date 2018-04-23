package com.my.rx.bo.rx;

import com.my.rx.bo.domain.User;
import com.my.rx.bo.report.api.BoReportProcessor;
import com.my.rx.bo.rx.api.BoTaskExecutor;
import com.my.rx.bo.rx.api.BoTaskProcessor;
import com.my.rx.bo.rx.domain.BoJobResult;
import com.my.rx.bo.rx.domain.BoTask;
import com.my.rx.bo.rx.domain.BoTaskReport;
import org.junit.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

import static java.lang.String.format;

public class RxTaskExecutor implements BoTaskExecutor {

    private BoTaskProcessor taskProcessor;
    private BoReportProcessor reportProcessor;
    private BoTask task;

    @Override
    public void setTaskProcessor(BoTaskProcessor taskProcessor) {
        this.taskProcessor = taskProcessor;
    }

    @Override
    public <T, R> void setReportProcessor(BoReportProcessor<T, BoTaskReport<R>> reportProcessor) {
        this.reportProcessor = reportProcessor;
    }

    @Override
    public void setTask(BoTask task) {
        this.task = task;
    }

    @Override
    public void run() {
        Assert.assertNotNull("The Task can not be Null!", task);
        Assert.assertNotNull("The Task processor can not be Null!", taskProcessor);

        Collection<User> data = task.job.data.data;

        System.out.println(format("=======> Send event '%s' STARTED.", task.attribute));
        System.out.println(format("items size: %d", data.size()));

        Collection<BoJobResult<User>> lsJobResult = taskProcessor.executeTask(task, data);

        if (!CollectionUtils.isEmpty(lsJobResult) && reportProcessor != null) {
            writeReport(task, lsJobResult);
        }
    }

    private void writeReport(BoTask task, Collection<BoJobResult<User>> lsJobResult) {
        reportProcessor.write(new BoTaskReport<>(task.attribute, lsJobResult));
    }
}

    /*

    Observable.just(lsJobResult)
//                .asObservable()
                .subscribe(
                        (incomingValue) -> System.out.println("incomingValue " + incomingValue),
                        (error) -> System.out.println("Something went wrong" + error.getMessage()),
                        () -> System.out.println("This observable is finished")
                );

       private void executeTask(BoTask task, Collection<User> data) {
        Scheduler procCoreExecutor = Schedulers.from(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
        Scheduler singleExecutor = Schedulers.from(Executors.newSingleThreadExecutor());
        Scheduler ioExecutor = Schedulers.io();

        Observable.from(data)
//                .subscribeOn(singleExecutor)
                .window(threadsCount)
                .subscribe(
                        this::processBatchItems,

                        (e) -> {
                            System.out.println(format("=======> Send event COMPLETED '%s' was failure: %s.", task.attribute, e.getMessage()));
                        },

                        () -> {
                            System.out.println(format("=======> Send event COMPLETED '%s' was success.", task.attribute));
                        }
                );
    }

    private void processBatchItems(Observable<User> user) {
        user.flatMap(
                data ->
                        Observable.just(jobExecutor.execute(data)))
//                .observeOn(getScheduler())
//                .subscribeOn(getScheduler())
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
                );
    }

    private Scheduler getScheduler() {
        return threadsCount > 1
                ? Schedulers.from(Executors.newFixedThreadPool(threadsCount))
                : Schedulers.from(Executors.newSingleThreadExecutor());
    }*/