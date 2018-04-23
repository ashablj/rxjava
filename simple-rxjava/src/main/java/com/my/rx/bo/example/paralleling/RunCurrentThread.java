package com.my.rx.bo.example.paralleling;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * http://stackoverflow.com/questions/29355741/rxjava-observing-on-calling-subscribing-thread
 */
public class RunCurrentThread implements Executor {

    private BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();

    public static void main(String[] args) throws InterruptedException {
        RunCurrentThread sample = new RunCurrentThread();
        sample.observerOnMain();
        sample.runLoop();
    }

    private void observerOnMain() {
        createObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.from(this))
                .subscribe(message -> {
                    System.out.println("Observer thread " + message);
                    System.out.println("Observable thread " + Thread.currentThread().getName());
                });
        ;
    }

    public Observable<String> createObservable() {
        return Observable.create(
                (Subscriber<? super String> subscriber) -> {
                    subscriber.onNext(Thread.currentThread().getName());
                    subscriber.onCompleted();
                }
        );
    }

    private void runLoop() throws InterruptedException {
        while (!Thread.interrupted()) {
            tasks.take().run();
        }
    }

    @Override
    public void execute(Runnable command) {
        tasks.add(command);
    }
}