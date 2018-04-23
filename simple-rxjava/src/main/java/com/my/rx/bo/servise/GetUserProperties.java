package com.my.rx.bo.servise;

import com.google.common.collect.Lists;
import com.my.rx.bo.utils.BaseDataProvider;
import com.my.rx.bo.domain.User;
import com.my.rx.bo.strategies.GetUserBalance;
import com.my.rx.bo.strategies.GetUserExperience;
import com.my.rx.bo.strategies.GetUserLevel;
import rx.Observable;

import java.util.Date;
import java.util.List;

public class GetUserProperties extends BaseDataProvider {

    public static void main(String[] args) {
        System.out.println("Main start....." + new Date());

        getProperties(generateUsers());

        System.out.println("Main end....." + new Date());
    }

    private static void getProperties(List<User> users) {
        List<User> lsSuccessUsers = Lists.newArrayList();
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
                                    .subscribe(
                                            resultUser -> {
                                                lsSuccessUsers.add(resultUser);
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

        printReport(lsSuccessUsers, lsErrorUsers);
        printUsers(lsSuccessUsers);
    }

    private static Observable<User> getZipObservable(User user, Observable<User> userLevel, Observable<User> userBalance, Observable<User> userExperience) {
        return Observable.zip(
                userLevel,
                userBalance,
                userExperience,
                (user1, user2, user3) -> {
                    User rez = new User(user.id, user1.level, user2.balance, user3.experience);
                    System.out.println("inner zip function returns: " + rez);
                    return rez;
                });
    }
}