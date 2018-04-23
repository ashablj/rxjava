package com.my.rx.bo.example.samples;

import rx.Observable;
import rx.Subscriber;

//  Processing halted when error encountered
public class ObservableHaltedError {

    public static void main(String[] args) {
        // In Java 8
        Observable.OnSubscribe<String> subscribeFunction = (s) -> produceValuesAndAnError(s);

        Observable.create(subscribeFunction)
                .subscribe(
                        (incomingValue) -> System.out.println("incoming " + incomingValue),
                        (error) -> System.out.println("Something went wrong " + ((Throwable) error).getMessage()),
                        () -> System.out.println("This observable is finished")
                );
    }

    private static void produceValuesAndAnError(Subscriber s) {
        Subscriber subscriber = (Subscriber) s;

        try {
            for (int ii = 0; ii < 50; ii++) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext("Pushed value " + ii);
                }

                if (ii == 5) {
                    throw new Throwable("Something has gone wrong here");
                }
            }

            if (!subscriber.isUnsubscribed()) {
                subscriber.onCompleted();
            }

        } catch (Throwable throwable) {
            subscriber.onError(throwable);
        }
    }
}
