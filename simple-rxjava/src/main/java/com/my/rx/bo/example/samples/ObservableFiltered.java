package com.my.rx.bo.example.samples;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

//  A Filtered Asynchronous Observable
public class ObservableFiltered {

    public static void main(String[] args) {
        // In Java 8
        Observable.OnSubscribe<String> subscribeFunction = (s) -> asyncProcessingOnSubscribe(s);

        Observable asyncObservable = Observable.create(subscribeFunction);

//        asyncObservable.skip(5).subscribe((incomingValue) -> System.out.println(incomingValue));
        asyncObservable.skip(5).subscribe(new Action1<String>() {
            @Override
            public void call(String incomingValue) {
                System.out.println(incomingValue);
            }
        });
    }

    private static void asyncProcessingOnSubscribe(Subscriber s) {
        final Subscriber subscriber = (Subscriber) s;
        Thread thread = new Thread(() -> produceSomeValues(subscriber));
        thread.start();
    }

    private static void produceSomeValues(Subscriber subscriber) {
        for (int ii = 0; ii < 10; ii++) {
            if (!subscriber.isUnsubscribed()) {
                subscriber.onNext("Pushing value from async thread " + ii);
            }
        }
    }
}
