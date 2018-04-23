package com.my.rx.bo.rx;

import com.my.rx.bo.domain.User;
import com.my.rx.bo.report.ConsoleReportProcessor;
import com.my.rx.bo.report.api.BoReportProcessor;
import com.my.rx.bo.rx.api.BoJobWorker;
import com.my.rx.bo.rx.api.BoTaskExecutor;
import com.my.rx.bo.rx.api.BoTaskProcessor;
import com.my.rx.bo.rx.domain.*;
import com.my.rx.bo.rx.strategy.processor.Simple2RxTaskProcessor;
import com.my.rx.bo.rx.strategy.worker.RxUpdateUserLevelAndBalance;
import com.my.rx.bo.utils.BaseDataProvider;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.String.format;

public class AppRunner extends BaseDataProvider {

    //    private static final int WORKERS_THREAD_COUNT = 4;
    private static Scheduler procCoreScheduler = Schedulers.from(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
    private static Scheduler singleScheduler = Schedulers.from(Executors.newSingleThreadExecutor());
    private static Scheduler ioScheduler = Schedulers.io();
    private static Scheduler fixedPoolScheduler = Schedulers.from(Executors.newFixedThreadPool(4));


    private static BoTaskProcessor taskProcessor = new Simple2RxTaskProcessor();
    private static BoJobWorker<User> workerStrategy = new RxUpdateUserLevelAndBalance();
    private static BoReportProcessor<String, BoTaskReport<Collection<BoJobResult<User>>>> reportStrategy = new ConsoleReportProcessor<>();
    private static ExecutorService generalExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static void main(String[] args) {
        System.out.println(format("################# App started at %s #################\n", new Date()));

        generalExecutorService.execute(createTaskExecutor());

//        ThreadUtils.sleep(20, TimeUnit.SECONDS);
        System.out.println(format("################# App finished at %s #################", new Date()));
        generalExecutorService.shutdown();
    }

    private static BoTaskExecutor createTaskExecutor() {
        taskProcessor.setJobWorker(workerStrategy);
        taskProcessor.setScheduler(Schedulers.trampoline());

        BoTaskExecutor taskExecutor = new RxTaskExecutor();
        taskExecutor.setTaskProcessor(taskProcessor);
        taskExecutor.setTask(createTask());
        taskExecutor.setReportProcessor(reportStrategy);
        return taskExecutor;
    }

    private static BoTask createTask() {
        return new BoTask(
                new BoTaskAttribute(
                        "Update user properties",
                        AppRunner.class.getSimpleName(),
                        123L),
                createJob());
    }

    private static BoJob createJob() {
        BoJob job = new BoJob();
        job.data = new BoJobData(generateUsers());
        return job;
    }

    /*private Scheduler createScheduler() {
        return workersPoolSize > 1
                ? Schedulers.from(Executors.newFixedThreadPool(workersPoolSize))
                : Schedulers.from(Executors.newSingleThreadExecutor());
    }*/
}
