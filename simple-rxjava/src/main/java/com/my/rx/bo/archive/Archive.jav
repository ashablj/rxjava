package com.my.rx.bo;

import com.google.common.collect.Lists;
import com.my.rx.bo.domain.User;
import com.my.rx.bo.service.UpdateUserBalance;
import com.my.rx.bo.service.UpdateUserLevel;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Archive {

    private static final int batchSize = 7;

    public static void main(String[] args) throws InterruptedException {
        Date startTs = new Date();
        System.out.println("Main started....");

        test1();

        Date endTs = new Date();
        System.out.println("Main complete.... " + (startTs.getTime() - endTs.getTime()));
    }

    public static UpdateUserBalance updateUserBalance = new UpdateUserBalance();
    public static UpdateUserLevel updateUserLevel = new UpdateUserLevel();

    private static void test1() {
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
//                                    .timeout(200, TimeUnit.MILLISECONDS)
//                                    .toBlocking()
                                    .forEach(System.out::println);
                        },

                        (e) -> {
                            System.out.println("\nMain error: " + e.getMessage());
                        },

                        () -> {
                            System.out.println("\nMain complete!");
                        }
                );
    }

    private static void mergingSyncMadeAsync() {
        // if you have something synchronous and want to make it async, you can schedule it like this
        // so here we see both executed concurrently
        Observable.merge(
                getDataSync(1).subscribeOn(Schedulers.io()),
                getDataSync(2).subscribeOn(Schedulers.io()))
                .toBlocking()
                .forEach(System.out::println);
    }


    private static Observable<UpdateUserBalance> makeUpdateUserBalance(User data) {
        return Observable.create(subscriber1 ->
                updateUserBalance.update(data.id, data.balance));
    }

    private static Observable<UpdateUserLevel> makeUpdateUserLevel(User data) {
        return Observable.create(subscriber1 ->
                updateUserLevel.update(data.id, data.level));
    }

    static Observable<Integer> getDataSync(int i) {
        return Observable.create((Subscriber<? super Integer> s) -> {
            // simulate latency
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            s.onNext(i);
            s.onCompleted();
        });
    }


    private static Observable<User> makeChunk(List<User> source, Integer numberItem) {
        return Observable.from(source)
                .skip(numberItem * batchSize)
                .take(batchSize);

    }

    private static List<User> generateUsers() {
        return Arrays.asList(
                new User().generate(),
                new User().generate(),

//                null,
                new User().generate(),
                new User().generate(),
                new User().generate(),
                new User().generate(),
                new User().generate(),
                new User().generate(),
                new User().generate(),
                new User().generate(),
                new User().generate(),
                new User().generate(),
                new User().generate(),
                new User().generate(),
                new User().generate(),
                new User().generate(),
                new User().generate());
    }

    private static void test2() {
        final int maxNumb = 17;
        int chunkCount = maxNumb / batchSize;
        System.out.println(chunkCount);

        Observable.range(0, chunkCount)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onNext(Integer item) {
                        System.out.println("Chunk number: " + item);
                        List<Integer> ls = Lists.newArrayList();
                        Observable.range(1, maxNumb)
                                .skip(item * batchSize)
                                .take(batchSize)
                                .subscribe(
                                        ls::add
                                );
                        System.out.println("send data " + ls + "!");
                    }

                    @Override
                    public void onError(Throwable error) {
                        System.err.println("Error: " + error.getMessage());
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("Sequence complete.");
                    }
                });
    }

    private static void test3() {
        Observable.range(1, 1000000)
                .sample(7, TimeUnit.MILLISECONDS)
                .forEach(System.out::println);
    }

    private static void backup() {
        List<User> userses = Arrays.asList(
                new User().generate(),
                new User().generate(),
//                null,
                new User().generate(),
                new User().generate(),
                new User().generate(),
                new User().generate(),
                new User().generate(),
                new User().generate(),
                new User().generate(),
                new User().generate(),
                new User().generate(),
                new User().generate(),
                new User().generate(),
                new User().generate(),
                new User().generate(),
                new User().generate());

        Observable
                .from(userses)
                .limit(batchSize)
//                .takeUntil(d -> d == null)
//                .doOnEach()
//                .window(batchSize)
//                .repeat(2)
//                .retry()
                /*.onErrorReturn(data ->
                         new User().generate()
                )*/
//                .map(data -> data.id + 1)
               /* .onErrorResumeNext(data ->
                        Observable.just(new User().generate())
                )*/
//                .forEach(System.out::println);
//                .flatMap(window -> window.first())
                .subscribe(
                        data -> {
                            System.out.println("send data " + data + "!");
                        },
                        (e) -> {
                            System.out.println("error: " + e.getMessage());
                        },
                        () -> {
                            System.out.println("complete");
                        }
                );


//        System.out.println(userses);
    }
}


   ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void chunkProcessor(BoTask task, Collection<User> data) {
        int chunkCount = calculateChunkCount(data.size());
        System.out.println(format("items size: %d, chunk size: %d", data.size(), chunkCount));

        Observable.range(0, chunkCount)
                .subscribe(
                        item -> {
                            makeChunk(data, item)
                                    .flatMap(
                                            jobExecutor::execute)
                                    .subscribe(
                                            result -> System.out.println("returns: " + result),

                                            (e) -> System.out.println("error :" + e),

                                            () -> System.out.println("------------> Send event PROGRESS")
                                    );
                        },

                        (e) -> {
                            System.out.println(format("=======> Send event COMPLETED '%s' was failure: %s.", task.attribute, e.getMessage()));
                        },

                        () -> {
                            System.out.println(format("=======> Send event COMPLETED '%s' was success.", task.attribute));
                        }
                );
    }

    private int calculateChunkCount(int dataSize) {
        return (dataSize / threadsCount) + 1;
    }

    private Observable<User> makeChunk(Collection<User> source, Integer numberItem) {
        return Observable.from(source)
                .skip(numberItem * threadsCount)
                .take(threadsCount);
    }

    public void executeV1(BoTask task) {
        System.out.println(format("The task of: %s was STARTED.", task.attribute));

        Collection<User> data = task.job.data.data;
        Observable
                .from(data)
                .take(threadsCount)
                .flatMap(
                        item -> {
//                            executorObservable.subscribeOn(Schedulers.io());
                            return Observable.just(jobExecutor.execute(item));

                        })
//                .subscribeOn(Schedulers.io())
                .subscribe(
                        rez -> System.out.println("result: " + rez),

                        (e) -> System.out.println("error :" + e),

                        () -> System.out.println(format("The task of: '%s' was COMPLETED.", task.attribute))

                );
    }