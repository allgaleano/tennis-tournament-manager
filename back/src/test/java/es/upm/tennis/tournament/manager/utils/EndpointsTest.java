package es.upm.tennis.tournament.manager.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Endpoints Tests")
class EndpointsTest {

    @Test
    @DisplayName("Should contain expected public endpoints")
    void shouldContainExpectedPublicEndpoints() {
        List<String> expectedEndpoints = List.of(
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

        assertEquals(expectedEndpoints.size(), Endpoints.PUBLIC_ENDPOINTS.size());
        assertTrue(Endpoints.PUBLIC_ENDPOINTS.containsAll(expectedEndpoints));
    }

    @Test
    @DisplayName("Should have correct frontend URI")
    void shouldHaveCorrectFrontendUri() {
        assertEquals("http://localhost:3000", Endpoints.FRONTEND_URI);
    }
}