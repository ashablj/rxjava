package com.my.rx.bo.example.async;

import com.my.rx.bo.utils.ThreadUtils;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * http://www.java-allandsundry.com/2015/02/async-abstractions-using-rx-java.html
 */
public class Main {

    public static void main(String[] args) {
        System.out.println(check(5, 3));
        System.out.println(check(3.2 , 1));

        System.out.println(check(9999 , 8));
    }

    public static boolean check(double a, int b) {
        return (a % b) == 0;
    }
     /*   Observable<String> op1 = new Service1().operation();
        Observable<String> op2 = new Service1().operation();
        Observable<String> op3 = new Service1().operation();

        Observable<List<String>> lst = Observable.merge(op1, op2, op3).toList();
        lst.subscribe();

        ThreadUtils.sleep(9000);
    }*/
}

class Service1 {
    public Observable<String> operation() {
        return Observable.<String>create(s -> {
            System.out.println("Start: Executing slow task in Service" + Thread.currentThread().getName());
//            Util.delay(7000);
            ThreadUtils.sleep(2000);
            s.onNext("operation 1");
            System.out.println("End: Executing slow task in Service" + Thread.currentThread().getName());
            s.onCompleted();
        }).subscribeOn(Schedulers.computation());
    }
}
