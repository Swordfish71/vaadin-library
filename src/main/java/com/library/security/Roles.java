package com.library.security;

public interface Roles {
    String ADMIN = "ADMIN";
    String USER = "USER";

    // Spring Security shorthands for method level security
    String IS_ADMIN = "hasRole('" + ADMIN + "')";
}

