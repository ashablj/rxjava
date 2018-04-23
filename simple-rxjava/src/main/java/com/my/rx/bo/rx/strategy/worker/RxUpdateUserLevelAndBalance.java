package com.my.rx.bo.rx.strategy.worker;

import com.my.rx.bo.domain.User;
import com.my.rx.bo.rx.api.BoJobWorker;
import com.my.rx.bo.rx.domain.BoJobResult;
import com.my.rx.bo.rx.enums.RxJobStatus;
import com.my.rx.bo.strategies.UpdateUserBalance;
import com.my.rx.bo.strategies.UpdateUserLevel;
import com.my.rx.bo.utils.ThreadUtils;

import java.util.Random;

import static java.lang.String.format;

public class RxUpdateUserLevelAndBalance implements BoJobWorker<User> {

    private static final int MS = 300;
    private final UpdateUserLevel updateUserLevel = new UpdateUserLevel();
    private final UpdateUserBalance updateUserBalance = new UpdateUserBalance();

    @Override
    public BoJobResult<User> execute(User user) {
        BoJobResult<User> result = new BoJobResult<>(user);

        try {
            boolean isUpdated = new Random().nextBoolean()
                    && updateUserLevel.update(user.id, user.level)
                    && updateUserBalance.update(user.id, user.balance);

            result.status = isUpdated
                    ? RxJobStatus.SUCCESS
                    : RxJobStatus.ERROR;

        } catch (Exception e) {
            String message = format("Logging %s:%s was error: %s \nAll values was rollback.", getClass().getSimpleName(), user, e.getMessage());
            System.out.println(message);
            result.errorMessage = message;
            result.status = RxJobStatus.FAILURE;
        }

        ThreadUtils.sleep(MS);
        return result;
    }
}