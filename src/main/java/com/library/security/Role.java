package com.library.security;

public enum Role {
    ADMIN(RoleConstants.ADMIN), USER(RoleConstants.USER);

    private final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    public static class RoleConstants {
        public static final String ADMIN = "ADMIN";
        public static final String USER = "USER";
    }
}

