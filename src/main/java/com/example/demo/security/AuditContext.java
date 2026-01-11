package com.example.demo.security;

public class AuditContext {

    private static final ThreadLocal<String> CURRENT_USER_UUID = new ThreadLocal<>();

    public static void setCurrentUser(String userUuid) {
        CURRENT_USER_UUID.set(userUuid);
    }

    public static String getCurrentUser() {
        return CURRENT_USER_UUID.get() != null ? CURRENT_USER_UUID.get() : "system";
    }

    public static void clear() {
        CURRENT_USER_UUID.remove();
    }
}
