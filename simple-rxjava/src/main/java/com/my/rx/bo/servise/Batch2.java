package com.my.rx.bo.servise;

import com.google.common.collect.Lists;
import com.my.rx.bo.utils.BaseDataProvider;
import com.my.rx.bo.domain.Coupon;
import com.my.rx.bo.domain.User;
import rx.Observable;

import java.util.List;

import static java.lang.String.format;

public class Batch2 extends BaseDataProvider {

    private static final int batchSize = 7;

    public static void main(String[] args) {
        test1();
    }

    private static void test1() {
        List<User> users = generateUsers();
        List<Coupon> coupons = generateCoupons();

        int chunkCount = (users.size() / batchSize) + 1;
        System.out.println(format("items count: %d, chunk count: %d", users.size(), chunkCount));

        Observable.from(coupons)
                .subscribe(
                        data -> sub(data.name, users, chunkCount),
                        (e) -> System.out.println("error: " + e.getMessage()),
                        () -> System.out.println("\nMain complete!")
                );
    }

    private static void sub(String templateName, List<User> users, int chunkCount) {
        Observable.range(0, chunkCount)
                .subscribe(
                        item -> {
                            List<User> lsUsers = Lists.newArrayList();
                            makeChunk(users, item)
                                    .subscribe(
                                            lsUsers::
                                                    add,
                                            (e) ->
                                                    System.out.println("error: " + e.getMessage()),
                                            () -> {
                                                sendMethod(templateName, lsUsers);
                                                System.out.println(format("send template: %s, chunk size: %d was completed", templateName, lsUsers.size()));
                                            }
                                    );
                        },

                        (e) -> {
                            System.out.println("\nMain error: " + e.getMessage());
                        },

                        () -> {
                            System.out.println("\nMain complete!");
                        }
                );
    }

    private static void sendMethod(String templateName, List<User> lsUsers) {
        System.out.println(format("send template: %s, data: %s", templateName, lsUsers));
    }

    private static Observable<User> makeChunk(List<User> source, Integer numberItem) {
        return Observable.from(source)
                .skip(numberItem * batchSize)
                .take(batchSize);

    }
}