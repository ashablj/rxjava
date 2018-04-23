package com.my.rx.bo.example.scheduller;

import com.my.rx.bo.utils.ThreadUtils;
import rx.Scheduler;
import rx.functions.Action0;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

public class Main {

    public static void main(String[] args) {
        System.out.println("Main start....." + new Date());

        myWorker();
//        test2();
//        test3();

        System.out.println("Main end....." + new Date());
    }

    private static void myWorker() {
        Scheduler scheduler = Schedulers.newThread();
        Scheduler.Worker worker = scheduler.createWorker();

        worker = Schedulers.newThread().createWorker();
        worker.schedule(() -> printThread("In progress 1"));
        worker.schedule(() -> printThread("In progress 2"));
        worker.schedule(() -> printThread("In progress 3"));
        worker.schedule(() -> printThread("In progress 4"));
        worker.schedule(() -> printThread("In progress 5"));


       while (!worker.isUnsubscribed()) {
            ThreadUtils.sleep(50);
            worker.unsubscribe();
        }

//        worker.unsubscribe();
    }

    private static void test3() {
        printThread("Main");
        Scheduler scheduler = Schedulers.newThread();
        Scheduler.Worker worker = scheduler.createWorker();
        worker.schedule(() -> {
            printThread("Start");
            worker.schedule(() -> printThread("Inner"));
            printThread("End");
        });
        ThreadUtils.sleep(1200);
        worker.schedule(() -> printThread("Again"));
    }

    private static void test2() {
        Scheduler scheduler = Schedulers.newThread();
//        Scheduler scheduler = Schedulers.from(Executors.newFixedThreadPool(5));

//        worker.schedule(
//                () ->
//                        System.out.println(format("[Thread: %s] In Action", Thread.currentThread().getName()))
//                );

        long start = System.currentTimeMillis();
        Scheduler.Worker worker = scheduler.createWorker();
        worker.schedule(
                () -> System.out.println(System.currentTimeMillis() - start),
                5, TimeUnit.SECONDS);
        worker.schedule(
                () -> System.out.println(System.currentTimeMillis() - start),
                5, TimeUnit.SECONDS);
    }

    private static void testInterruptTerminalEventAwait() {
        TestSubscriber<Integer> ts = TestSubscriber.create();

        final Thread t0 = Thread.currentThread();
        Scheduler.Worker w = Schedulers.computation().createWorker();
        try {
            w.schedule(new Action0() {
                @Override
                public void call() {
                    t0.interrupt();
                }
            }, 2000, TimeUnit.MILLISECONDS);

            try {
                ts.awaitTerminalEvent();
                fail("Did not interrupt wait!");
            } catch (RuntimeException ex) {
                if (!(ex.getCause() instanceof InterruptedException)) {
                    fail("The cause is not InterruptedException! " + ex.getCause());
                }
            }
        } finally {
            w.unsubscribe();
        }
    }

    private static void fail(String s) {
        System.out.println(s);
    }

    public static void printThread(String message) {
        System.out.println(format("[Thread name: %s] message: %s ", Thread.currentThread().getId(), message));
    }
}
