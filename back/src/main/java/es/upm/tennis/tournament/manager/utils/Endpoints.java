package es.upm.tennis.tournament.manager.utils;

import java.util.List;

public class Endpoints {
    public static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/auth/register",
            "/auth/login",
            "/auth/confirm-email",
            "/auth/forgot-password",
            "/auth/change-password"
    );
}
