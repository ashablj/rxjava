package com.my.rx.bo.example.samples;

import rx.Observable;
import rx.Subscriber;

// Creating an Observable via the create() method
public class ObservableViaMethod {

    public static void main(String[] args) {
        // In Java 8
        Observable.OnSubscribe<String> subscribeFunction = (s) -> {
            Subscriber subscriber = (Subscriber) s;

            for (int ii = 0; ii < 10; ii++) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext("Pushed value " + ii);
                }
            }

            if (!subscriber.isUnsubscribed()) {
                subscriber.onCompleted();
            }
        };

        Observable createdObservable = Observable.create(subscribeFunction);

        createdObservable.subscribe(
                (incomingValue) -> System.out.println("incomingValue " + incomingValue),
                (error) -> System.out.println("Something went wrong" + ((Throwable) error).getMessage()),
                () -> System.out.println("This observable is finished")
        );
    }
}
