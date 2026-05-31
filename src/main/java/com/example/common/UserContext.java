package com.example.common;

import lombok.Data;

/**
 * 当前登录用户信息，通过 ThreadLocal 传递
 */
@Data
public class UserContext {

    private Long userId;
    private String username;
    private String role;

    private static final ThreadLocal<UserContext> CURRENT = new ThreadLocal<>();

    public static void set(UserContext ctx) {
        CURRENT.set(ctx);
    }

    public static UserContext get() {
        return CURRENT.get();
    }

    public static Long getUserId() {
        UserContext ctx = CURRENT.get();
        return ctx != null ? ctx.userId : null;
    }

    public static String getRole() {
        UserContext ctx = CURRENT.get();
        return ctx != null ? ctx.role : null;
    }

    public static void clear() {
        CURRENT.remove();
    }
}
