package com.my.rx.bo.archive;

import com.my.rx.bo.domain.User;
import com.my.rx.bo.rx.domain.BoJobResult;
import com.my.rx.bo.rx.enums.RxJobStatus;
import com.my.rx.bo.strategies.UpdateUserBalance;
import com.my.rx.bo.strategies.UpdateUserLevel;
import com.my.rx.bo.utils.ThreadUtils;
import rx.Observable;

import java.util.Random;

import static java.lang.String.format;

public class UpdateUserLevelAndBalanceJobExecutor { //implements BoJobWorker<User>

    private final UpdateUserLevel updateUserLevel = new UpdateUserLevel();
    private final UpdateUserBalance updateUserBalance = new UpdateUserBalance();

    //    @Override
    public Observable<BoJobResult<User>> execute(User user) {
        BoJobResult<User> result = new BoJobResult<>(user);

        try {
            boolean isUpdated = new Random().nextBoolean()
                    && updateUserLevel.update(user.id, user.level)
                    && updateUserBalance.update(user.id, user.balance);

            result.status = isUpdated
                    ? RxJobStatus.SUCCESS
                    : RxJobStatus.ERROR;

        } catch (Exception e) {
            String msg = format("Logging %s:%s was error: %s", getClass().getSimpleName(), user, e.getMessage());
            System.out.println(msg);
            System.out.println("Values aws rollback.");
            result.errorMessage = msg;
        }

        ThreadUtils.sleep(800);
        return Observable.just(result);
    }

    public Observable<Boolean> execute2(User user) {
        ThreadUtils.sleep(800);
        return Observable.just(user)
                .flatMap(
                        item -> {
                            Observable<Boolean> updateLevelObservable = Observable.just(updateUserLevel.update(user.id, user.level));
//                            Observable<Boolean> updateLevelObservable = ObservableFactory.createBooleanType(updateUserLevel.update(user.id, user.level));
                            Observable<Boolean> updateBalanceObservable = Observable.just(updateUserBalance.update(user.id, user.balance));
//                            Observable<Boolean> updateBalanceObservable = ObservableFactory.createBooleanType(updateUserBalance.update(user.id, user.balance));
                            return Observable.zip(
                                    updateLevelObservable,
                                    updateBalanceObservable,
                                    (level, balance) -> {
                                        System.out.println(format("inner function returns: %s, %s", level, balance));
                                        return level && balance;
                                    });
                        }
                );
    }
}

/// IllegalArgumentException

/*class UpdateUserBalance {
    public void update(long userId, double balance) {
        System.out.println(format("Service %s for userId: %d, balance: %s was success", getClass().getSimpleName(), userId, balance));
    }
}

class UpdateUserLevel {
    public void update(long userId, int level) {
        System.out.println(format("Service %s for userId: %d, level: %d was success", getClass().getSimpleName(), userId, level));

    }
}*/

/*class ObservableFactory {
    public static <T> Observable<Boolean> createBooleanType(T method) {
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
}*/
