package com.my.rx.bo.utils;

import java.util.concurrent.TimeUnit;

public class ThreadUtils {

    public static void sleep(int ms) {
        try {
            TimeUnit.MILLISECONDS.sleep(ms);
        } catch (InterruptedException ignored) {
        }
    }

    public static void sleep(long time, TimeUnit timeUnit) {
        try {
            timeUnit.sleep(time);
        } catch (InterruptedException ignored) {
        }
    }
}
