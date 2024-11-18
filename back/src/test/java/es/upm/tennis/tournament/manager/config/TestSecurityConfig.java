package es.upm.tennis.tournament.manager.config;

import es.upm.tennis.tournament.manager.model.ERole;
import es.upm.tennis.tournament.manager.model.Role;
import es.upm.tennis.tournament.manager.model.User;
import es.upm.tennis.tournament.manager.model.UserSession;
import es.upm.tennis.tournament.manager.service.UserSessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    @Primary
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Primary
    public AuthenticationManager authenticationManager() {
        return authentication -> authentication; // Returns a default authentication for testing
    }

    @Bean
    @Primary
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }

    @ExtendWith(MockitoExtension.class)
    @DisplayName("Session Filter Tests")
    static
    class SessionFilterTest {

        @Mock
        private UserSessionService sessionService;

        @Mock
        private FilterChain filterChain;

        @InjectMocks
        private SessionFilter sessionFilter;

        private MockHttpServletRequest request;
        private MockHttpServletResponse response;
        private User user;
        private UserSession userSession;

        @BeforeEach
        void setUp() {
            request = new MockHttpServletRequest();
            response = new MockHttpServletResponse();
            SecurityContextHolder.clearContext();

            Role role = new Role();
            role.setType(ERole.USER);

            user = new User();
            user.setId(1L);
            user.setUsername("testUser");
            user.setRole(role);

            userSession = new UserSession();
            userSession.setSessionId("valid-session-id");
            userSession.setUser(user);
        }

        @Nested
        @DisplayName("Public Endpoints Tests")
        class PublicEndpointsTests {
            @Test
            @DisplayName("Should allow access to public endpoints without session")
            void doFilter_ShouldAllowAccess_WhenPublicEndpoint() throws Exception {
                request.setRequestURI("/auth/login");

                sessionFilter.doFilterInternal(request, response, filterChain);

                verify(filterChain).doFilter(request, response);
                assertNull(SecurityContextHolder.getContext().getAuthentication());
            }
        }

        @Nested
        @DisplayName("Protected Endpoints Tests")
        class ProtectedEndpointsTests {
            @Test
            @DisplayName("Should authenticate user with valid session")
            void doFilter_ShouldAuthenticate_WhenValidSession() throws Exception {
                request.setRequestURI("/api/protected");
                request.addHeader("Session-Id", "valid-session-id");

                when(sessionService.validateSession("valid-session-id")).thenReturn(true);
                when(sessionService.findBySessionId("valid-session-id")).thenReturn(userSession);

                sessionFilter.doFilterInternal(request, response, filterChain);

                verify(filterChain).doFilter(request, response);
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                assertNotNull(auth);
                assertEquals(user, auth.getPrincipal());
            }

            @Test
            @DisplayName("Should return unauthorized for invalid session")
            void doFilter_ShouldReturnUnauthorized_WhenInvalidSession() throws Exception {
                request.setRequestURI("/api/protected");
                request.addHeader("Session-Id", "invalid-session-id");

                when(sessionService.validateSession("invalid-session-id")).thenReturn(false);

                sessionFilter.doFilterInternal(request, response, filterChain);

                assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
                verify(filterChain, never()).doFilter(request, response);
            }

            @Test
            @DisplayName("Should return unauthorized when no session provided")
            void doFilter_ShouldReturnUnauthorized_WhenNoSession() throws Exception {
                request.setRequestURI("/api/protected");

                sessionFilter.doFilterInternal(request, response, filterChain);

                assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
                verify(filterChain, never()).doFilter(request, response);
            }
        }

        @Nested
        @DisplayName("Security Context Tests")
        class SecurityContextTests {
            @Test
            @DisplayName("Should set correct authorities in security context")
            void doFilter_ShouldSetCorrectAuthorities_WhenValidSession() throws Exception {
                request.setRequestURI("/api/protected");
                request.addHeader("Session-Id", "valid-session-id");

                when(sessionService.validateSession("valid-session-id")).thenReturn(true);
                when(sessionService.findBySessionId("valid-session-id")).thenReturn(userSession);

                sessionFilter.doFilterInternal(request, response, filterChain);

                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                assertNotNull(auth);
                assertTrue(auth.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
            }

            @Test
            @DisplayName("Should clear security context when session invalid")
            void doFilter_ShouldClearContext_WhenSessionInvalid() throws Exception {
                request.setRequestURI("/api/protected");
                request.addHeader("Session-Id", "invalid-session-id");

                when(sessionService.validateSession("invalid-session-id")).thenReturn(false);

                sessionFilter.doFilterInternal(request, response, filterChain);

                assertNull(SecurityContextHolder.getContext().getAuthentication());
            }
        }
    }
}