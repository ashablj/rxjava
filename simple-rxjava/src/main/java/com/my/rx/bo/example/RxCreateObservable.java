package com.my.rx.bo.example;

import com.my.rx.bo.utils.BaseDataProvider;
import com.my.rx.bo.utils.ObservableFactory;
import rx.Observable;

import java.util.Date;
import java.util.function.Function;

public class RxCreateObservable extends BaseDataProvider {

    public static void main(String[] args) {
        System.out.println("Main start....." + new Date());

        test1();
        test2();
        test3();

        System.out.println("Main end....." + new Date());
    }

    private static void test1() {
        Observable<Boolean> just = Observable.just(new InnerClass().booleanMethod());
        just.subscribe(System.out::println);
    }

    private static void test2() {
        Observable<Boolean> just = ObservableFactory.create(new InnerClass().booleanMethod());
        just.subscribe(System.out::println);
    }


    private static void test3() {
        // example of create  Function
        Function<Void, Boolean> voidToInt = x -> true;

        Function<Integer, Boolean> voidToBoolean = x -> {
            new InnerClass().voidMethod();
            return true;
        };

        Observable<Boolean> just = createBoolean(voidToBoolean);
        just.subscribe(System.out::println);
    }

    private static Observable<Boolean> createBoolean(Function<Integer, Boolean> function) {
        return Observable.create(
                observer -> {
                    try {
                        observer.onNext(function.apply(0));
                        observer.onCompleted();
                    } catch (Exception e) {
                        observer.onError(e);
                    }
                });
    }
}

class InnerClass {

    public void voidMethod() {
        System.out.println(this.getClass().getSimpleName());
    }

    public boolean booleanMethod() {
        System.out.println(this.getClass().getSimpleName());
        return true;
    }
}