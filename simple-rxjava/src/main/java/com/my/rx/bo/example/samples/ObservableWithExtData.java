package com.my.rx.bo.example.samples;

import rx.Observable;

// Creating an Observable from an Existing Data Structure
public class ObservableWithExtData {

    public static void main(String[] args) {
        // In Java 8
        Integer[] numbers = {0, 1, 2, 3, 4, 5};

        Observable numberObservable = Observable.from(numbers);

        numberObservable.subscribe(
                (incomingNumber) -> System.out.println("incomingNumber " + incomingNumber),
                (error) -> System.out.println("Something went wrong" + ((Throwable) error).getMessage()),
                () -> System.out.println("This observable is finished")
        );
    }
}
