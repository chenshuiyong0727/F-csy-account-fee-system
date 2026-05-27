package com.yj.accountfee.common;

public final class CurrentUser {
    private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();

    private CurrentUser() {
    }

    public static void setUsername(String username) {
        USERNAME.set(username);
    }

    public static String username() {
        String username = USERNAME.get();
        return username == null ? "system" : username;
    }

    public static void clear() {
        USERNAME.remove();
    }
}
