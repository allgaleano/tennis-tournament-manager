package es.upm.tennis.tournament.manager.utils;

import java.util.List;

public class Endpoints {
    public static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/auth/register",
            "/auth/login",
            "/auth/confirm-email",
            "/auth/confirm-password",
            "/auth/change-password",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/webjars/**"
    );

    public static final String FRONTEND_URI = "http://localhost:3000";
}
