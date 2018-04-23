package com.my.rx.bo.example;

import com.my.rx.bo.utils.ThreadUtils;
import rx.Observable;

import java.util.Date;
import java.util.IllformedLocaleException;
import java.util.concurrent.TimeUnit;

public class ErrorHandlingExample {

    public static void main(String[] args) {
        System.out.println("Main start....." + new Date());

        test1();
//        test2();
//        test3();

        System.out.println("Main end....." + new Date());
    }

    /**
     * http://stackoverflow.com/questions/28390878/rxjava-observable-and-subscriber-for-skipping-exception
     */
    public static void test1() {
        Observable.just(1, 2, 3, 0, 4, 0, 6)
                .flatMap(i ->
                        Observable.defer(() -> Observable.just(12 / i))
//                        Observable.just(12 / i)
                                .onExceptionResumeNext(
                                        Observable.just(0)
                                                .doOnCompleted(
                                                        () -> System.out.println("Was exception returns '0' value.")))
                                .onErrorResumeNext(
                                        Observable.just(0)
                                                .doOnCompleted(
                                                        () -> System.out.println("Was error returns '0' value."))))
                .subscribe(System.out::println);
    }

    /**
     * https://github.com/ReactiveX/RxJava/issues/1046
     */
    public static void test2() {
        System.out.println("--------- default");
        // default error handling
        getStream()
                .subscribe(
                        ErrorHandlingExample::successHandler,
                        ErrorHandlingExample::errorHandler,
                        ErrorHandlingExample::completedHandler);

        System.out.println("\n--------- onErrorReturn");
        // onErrorReturn
        getStream()
//                .onExceptionResumeNext(ErrorHandlingExample::errorResumeNextHandler)
                .onErrorResumeNext(ErrorHandlingExample::errorResumeNextHandler)
                .onErrorReturn(ErrorHandlingExample::errorHandler)
                .doOnCompleted(ErrorHandlingExample::completedHandler)
                .subscribe(ErrorHandlingExample::successHandler);

    }

    public static Observable<Integer> getStream() {
        return Observable.just(1, 2, 3, -1, 4, 5, 8, -2, 11)
                .map(i -> {
                    if (i == -1) {
                        throw new RuntimeException("injected failure");
                    } else {
                        return i;
                    }
                });
    }

    private static void completedHandler() {
        System.out.println("Completed!");
    }

    private static void successHandler(int n) {
        System.out.println("Success: " + n);
    }

    private static Observable<Integer> errorResumeNextHandler(Throwable t) {
        System.out.println("\tError: " + t.getMessage());
        return Observable.just(101, 102, 103);
    }

    private static int errorHandler(Throwable t) {
        System.out.println("\tError: " + t.getMessage());
        return 99;
    }

    public static void test3() {
        Observable.interval(1, TimeUnit.SECONDS)
                .onExceptionResumeNext(Observable.just(1000L))
                .doOnError((e) -> Observable.just(1000L))
                .map(input -> {
                    if (Math.random() < .5) {
                        throw new IllformedLocaleException("sssss");
                    }
                    return "Success " + input;
                })
                .retry()
                .subscribe(System.out::println);

        ThreadUtils.sleep(5000);
    }
}