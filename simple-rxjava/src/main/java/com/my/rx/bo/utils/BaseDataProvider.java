package com.my.rx.bo.utils;

import com.my.rx.bo.domain.Coupon;
import com.my.rx.bo.domain.User;

import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

public class BaseDataProvider {

    public static List<Coupon> generateCoupons() {
        return Arrays.asList(
                new Coupon().generate(),
                new Coupon().generate(),
                new Coupon().generate(),
                new Coupon().generate(),
                new Coupon().generate(),
                new Coupon().generate(),
                new Coupon().generate(),
                new Coupon().generate());
    }

    public static List<User> generateUsers() {
        return Arrays.asList(
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
                new User().generate(),
                new User().generate(),
                new User().generate());
    }

    public static List<User> generateErrorUsers() {
        return Arrays.asList(
                new User().generate(),
                null,
                new User(0, 0, 0, 0),
                new User().generate(),
                null,
                new User().generate(),
                new User(0, 0, 0, 0),
                new User().generate(),
                null,
                null,
                new User().generate(),
                new User(0, 0, 0, 0),
                new User().generate(),
                new User().generate(),
                new User(0, 0, 0, 0),
                new User().generate(),
                null);
    }

    public static void printReport(List<User> lsSuccessUsers, List<User> lsFailureUsers) {
        StringBuffer report = new StringBuffer()
                .append("\n")
                .append("===== Report =========================================")
                .append("\n")
                .append(format("Success users count: %d", lsSuccessUsers.size()))
                .append("\n")
                .append(format("Failure users count: %d", lsFailureUsers.size()));

        System.out.println(report);
    }

    public static void printUsers(List<User> users) {
        StringBuffer report = new StringBuffer()
                .append("users:")
                .append("\n")
                .append(Arrays.toString(users.stream()
                        .map(u -> u + "\n")
                        .toArray()));

        System.out.println(report);
    }
}