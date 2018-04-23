package com.my.rx.bo.example;

import com.google.common.collect.Lists;
import com.my.rx.bo.strategies.*;
import com.my.rx.bo.utils.BaseDataProvider;
import com.my.rx.bo.domain.User;
import com.my.rx.bo.utils.ObservableFactory;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.Date;
import java.util.List;

import static java.lang.String.format;

public class RxCombining extends BaseDataProvider {

    public static void main(String[] args) {
        System.out.println("Main start....." + new Date());

        zip(generateUsers());
//        merge(generateUsers());

        System.out.println("Main end....." + new Date());
    }

    private static void zip(List<User> users) {
        List<User> lsUsers = Lists.newArrayList();
        List<User> lsErrorUsers = Lists.newArrayList();

        Observable.from(users)
                .take(12)
                .subscribe(data -> {
                            data.level = 0;
                            data.experience = 0;
                            data.balance = 0.0;

                            Observable<User> userLevelObservable = Observable.just(new GetUserLevel().get(data.id));
                            Observable<User> userBalanceObservable = Observable.just(new GetUserBalance().get(data.id));
                            Observable<User> userExperienceObservable = Observable.just(new GetUserExperience().get(data.id));

                            getZipObservable(data, userLevelObservable, userBalanceObservable, userExperienceObservable)
//                                    .doOnError( -> System.out.println("Error logging: "))
                                    .subscribe(
                                            resultUser -> {
                                                lsUsers.add(resultUser);
                                                System.out.println("send event IN_PROGRESS");
                                            },

                                            (e) -> {
                                                System.out.println("Logging error: " + e.getMessage());
                                                lsErrorUsers.add(data);
                                            }
                                    );
                        },

                        (e) ->
                                System.out.println("send event COMPLETED was FAILURE: " + e.getMessage()),

                        () ->
                                System.out.println("send event COMPLETED was SUCCESS")
                );

        System.out.println("--------------------------------------------------------------------");
//        lsUsers.forEach(
//                result -> System.out.println(format("result: %s \n--------------------", result)));
    }

    private static Observable<User> getZipObservable(User user, Observable<User> userLevel, Observable<User> userBalance, Observable<User> userExperience) {
        return Observable.zip(
                userLevel,
                userBalance,
                userExperience,
                (user1, user2, user3) -> {
                    User rez = new User(user.id, user1.level, user2.balance, user3.experience);
                    System.out.println("inner function returns: " + rez);
                    return rez;
                });
    }

    private static void merge(List<User> users) {
        Observable.from(users)
                .take(8)
                .flatMap(
                        data -> {
                            data.level = 0;
                            data.experience = 0;
                            data.balance = 0.0;

                            Observable<User> userLevelObservable = ObservableFactory.create(new GetUserLevel().get(data.id));//getUserLevelObservable(data.id);
                            Observable<User> userBalanceObservable = ObservableFactory.create(new GetUserBalance().get(data.id));//getUserBalanceObservable(data.id);

//                    return getZipObservable(data, userLevelObservable, userBalanceObservable, userExperienceObservable);
                            return getMergeObservable(userLevelObservable, userBalanceObservable);
                        })
                .subscribe(
                        result -> System.out.println(format("result: %s \n---------------------------------------------", result)));
    }

    private static Observable<User> getMergeObservable(Observable<User> userLevelObservable, Observable<User> userBalanceObservable) {
        return Observable.merge(
                userLevelObservable,
                userBalanceObservable);
    }

    private static Observable<User> getUserBalanceObservable(long userId) {
        return Observable.create(
                observer -> {
                    try {
                        observer.onNext(new GetUserBalance().get(userId));
                        observer.onCompleted();
                    } catch (Exception e) {
                        observer.onError(e);
                    }
                });
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static UpdateUserBalance updateUserBalance = new UpdateUserBalance();
    public static UpdateUserLevel updateUserLevel = new UpdateUserLevel();

    private static void test3() {
        List<User> users = generateUsers();

        System.out.println(String.format("items count: %d", users.size()));

        Observable.from(users)
                .take(7)
                .subscribe(
                        item -> {
                            Observable<UpdateUserBalance> updateUserBalanceObservable = makeUpdateUserBalance(item);
                            Observable<UpdateUserLevel> updateUserLevelObservable = makeUpdateUserLevel(item);
                            Observable<Object> merge = Observable.merge(
                                    updateUserBalanceObservable.subscribeOn(Schedulers.io()),
                                    updateUserLevelObservable.subscribeOn(Schedulers.io()));

                            merge
                                    .forEach(System.out::println);
                        },

                        (e) -> {
                            System.out.println("\ntest1 error: " + e.getMessage());
                        },

                        () -> {
                            System.out.println("\ntest1 complete!");
                        }
                );
    }

    private static Observable<UpdateUserBalance> makeUpdateUserBalance(User data) {
        return Observable.create(
                subscriber1 -> updateUserBalance.update(data.id, data.balance));
    }

    private static Observable<UpdateUserLevel> makeUpdateUserLevel(User data) {
        return Observable.create(
                subscriber1 -> updateUserLevel.update(data.id, data.level));
    }
}