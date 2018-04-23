package com.my.rx.bo.utils;

import rx.Observable;

public class ObservableFactory {

    public static <T> Observable<T> create(T method) {
        return Observable.create(
                observer -> {
                    try {
                        observer.onNext(method);
                        observer.onCompleted();
                    } catch (Exception e) {
                        observer.onError(e);
                    }
                });
    }
}
