package com.my.rx.bo.example;

import com.my.rx.bo.utils.BaseDataProvider;
import com.my.rx.bo.domain.User;
import rx.Observable;

import java.util.Date;
import java.util.List;

import static java.lang.String.format;

public class RxErrors extends BaseDataProvider {

    public static void main(String[] args) {
        System.out.println("Main start....." + new Date());

        test1(generateUsers());
        doNextHandler(new User(1001, 1, 0.0, 0));

        System.out.println("Main end....." + new Date());
    }

    private static void test1(List<User> data) {
        System.out.println(format("items count: %d", data.size()));

        Observable.from(data)
//                .onExceptionResumeNext(
//                        Observable.just(new User(0, 0, 0.0, 0)))
//                .onErrorResumeNext(
//                        Observable.just(new User(0, 0, 0.0, 0)))
//                .onErrorReturn(
//                        RxErrors::errorUserHandler)
                .subscribe(
                        RxErrors::doNextHandler);
               /* .subscribe(
                        item -> {
                            Observable.just(new UpdateLevel().update(item, item.id, item.level))
//                                    .doOnNext(
//                                            System.out::println)
//                                    .onExceptionResumeNext(
//                                            Observable.just(false))
//                                    .onErrorReturn(throwable -> {
//                                        System.out.println("calling onErrorReturn");
//                                        return false;
//                                    })
//                                    .onErrorResumeNext(
//                                            Observable.just(false))
//                                    .retry()
                                    .onErrorReturn(RxErrors::errorHandler)
                                    .subscribe(RxErrors::successHandler);
                        },

                        (e) -> {
                            System.out.println("\nMain error: " + e.getMessage());
                        },

                        () -> {
                            System.out.println("\nMain complete!");
                        }
                );*/
    }

    public static void doNextHandler(User item) {
//        System.out.println("Success: " + n);
        Observable.defer(() ->
                Observable.just(new UpdateLevel().update(item, item.id, item.level)))
                .onExceptionResumeNext(
                        Observable.just(false))
                .onErrorResumeNext(
                        Observable.just(false))
                .onErrorReturn(
                        RxErrors::errorHandler)
                .subscribe(
                        System.out::println);
    }

    public static void successHandler(boolean n) {
        System.out.println("Success: " + n);
    }

    private static User errorUserHandler(Throwable t) {
        System.out.println(format("\tError: %s, returns default value.", t.getMessage()));
        return new User(0, 0, 0.0, 0);
    }

    public static boolean errorHandler(Throwable t) {
        System.out.println(format("\tError: %s, returns default value.", t.getMessage()));
        return false;
    }
}

class UpdateLevel {
    public boolean update(User user, long userId, int level) {
        if (level < 4 || userId > 999) {
            String msg = format("An incorrect User values: %s", user);
            System.out.println(msg);
            throw new IllegalArgumentException(msg);
        }

        System.out.println(format("Service %s for user: %s was success", getClass().getSimpleName(), user));
        return true;
    }
}