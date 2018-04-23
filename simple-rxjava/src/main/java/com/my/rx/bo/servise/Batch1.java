package com.my.rx.bo.servise;

import com.google.common.collect.Lists;
import com.my.rx.bo.utils.BaseDataProvider;
import com.my.rx.bo.domain.User;
import rx.Observable;

import java.util.List;

public class Batch1 extends BaseDataProvider {

    private static final int batchSize = 7;

    public static void main(String[] args) {
        test1();
    }

    private static void test1() {
        List<User> users = generateUsers();

        int chunkCount = (users.size() / batchSize) + 1;
        System.out.println(String.format("items count: %d, chunk count: %d", users.size(), chunkCount));

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
                                                System.out.println("send data " + lsUsers);
                                                System.out.println("chunk size: " + lsUsers.size() + " was complete");
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

    private static Observable<User> makeChunk(List<User> source, Integer numberItem) {
        return Observable.from(source)
                .skip(numberItem * batchSize)
                .take(batchSize);

    }
}