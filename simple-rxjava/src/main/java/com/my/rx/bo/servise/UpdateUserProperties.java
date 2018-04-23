package com.my.rx.bo.servise;

import com.google.common.collect.Lists;
import com.my.rx.bo.utils.BaseDataProvider;
import com.my.rx.bo.domain.User;
import com.my.rx.bo.strategies.*;
import rx.Observable;

import java.util.Date;
import java.util.List;

import static java.lang.String.format;

public class UpdateUserProperties extends BaseDataProvider {

    private static final UpdateUserLevel updateUserLevel = new UpdateUserLevel();
    private static final UpdateUserExperience updateUserExperience = new UpdateUserExperience();
    private static final UpdateUserBalance updateUserBalance = new UpdateUserBalance();

    public static void main(String[] args) {
        System.out.println("Main started at: " + new Date() + "\n");

        update(generateUsers());

        System.out.println("\nMain was finished at: " + new Date());
    }

    private static void update(List<User> users) {
        List<User> lsSuccessUsers = Lists.newArrayList();
        List<User> lsFailureUsers = Lists.newArrayList();

        Observable.from(users)
                .take(12)
                .subscribe(data -> {
                            Observable<Boolean> userLevelObservable = Observable.just(updateUserLevel.update(data.id, data.level));
                            Observable<Boolean> updateExperienceObservable = Observable.just(updateUserExperience.update(data.id, data.level));
                            Observable<Boolean> updateBalanceObservable = Observable.just(updateUserBalance.update(data.id, data.level));

                            getZipObservable(data, userLevelObservable, updateExperienceObservable, updateBalanceObservable)
//                                    .doOnError(Throwable::printStackTrace)
//                                      .onErrorReturn(e -> new User(0, 0, 0.0, 0))
//                                    .onErrorResumeNext(Observable.just(true))
                                    .onExceptionResumeNext(Observable.just(false))
                                    .subscribe(
                                            resultOperation -> {
                                                boolean b = resultOperation
                                                        ? lsSuccessUsers.add(data)
                                                        : lsFailureUsers.add(data);

                                                System.out.println("send event IN_PROGRESS");
                                            },

                                            (e) -> {
                                                System.out.println("Logging error: " + e.getMessage());
                                                lsFailureUsers.add(data);
                                            }
                                    );
                        },

                        (e) ->
                                System.out.println("send event COMPLETED was FAILURE: " + e.getMessage()),

                        () ->
                                System.out.println("send event COMPLETED was SUCCESS")
                );

        printReport(lsSuccessUsers, lsFailureUsers);
    }

    private static Observable<Boolean> getZipObservable(User user, Observable<Boolean> userLevel, Observable<Boolean> userExperience, Observable<Boolean> userBalance) {
        return Observable.zip(
                userLevel,
                userExperience,
                userBalance,
                (level, experience, balance) -> {
                    System.out.println(format("inner function returns: %d, %s, %s, %s", user.id, level, experience, balance));
                    return level && experience && balance;
                });
    }
}