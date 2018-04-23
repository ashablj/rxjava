package com.my.rx.bo.example.paralleling;

import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.String.format;

public class Start {

    public static void main(String[] args) {
        System.out.println("Main start....." + new Date());

        test1();
//        testInterruptTerminalEventAwait();
//        testName();

        System.out.println("Main end....." + new Date());
    }

    private static void test1() {
        Scheduler coreExecutor = Schedulers.from(Executors.newFixedThreadPool(5));

        Observable.range(0, 999)
                .window(8)
                .flatMap(map -> Observable.just(map)
                        .observeOn(Schedulers.io()))

                .subscribeOn(coreExecutor)

                .subscribe(
                        r ->
                                System.out.println(format("[Thread: %s] returns result: %s", Thread.currentThread().getName(), r)),
                        (e) ->
                                System.out.println(format("[Thread: %s] returns error: %s", Thread.currentThread().getName(), e)),
                        () ->
                                System.out.println(format("--------------> [Thread: %s] COMPLETE", Thread.currentThread().getName()))
                );
    }

    public static void testName() {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        Observable
                .range(0, 99)
                .subscribeOn(Schedulers.io())
//                .subscribeOn(Schedulers.from(executor))
//                .interval(100, TimeUnit.MILLISECONDS, Schedulers.from(executor))
                .window(7)
                .toBlocking()
                .forEach(aLong -> {
                    aLong.forEach(val -> System.out.println(format("[Thread: %s] returns result: %s", Thread.currentThread().getName(), val)));
                    System.out.println(format("[Thread: %s] end part", Thread.currentThread().getName()));
                });
        executor.shutdown();
    }

    private static void fail(String s) {
        System.out.println(s);
    }

    public static void printThread(String message) {
        System.out.println(message + " on " + Thread.currentThread().getId());
    }
}
