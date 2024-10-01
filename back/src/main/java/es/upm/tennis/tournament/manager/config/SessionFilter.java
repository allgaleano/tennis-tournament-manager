package es.upm.tennis.tournament.manager.config;

import es.upm.tennis.tournament.manager.model.UserSession;
import es.upm.tennis.tournament.manager.model.User;
import es.upm.tennis.tournament.manager.service.UserSessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SessionFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(SessionFilter.class);
    @Autowired
    private UserSessionService sessionService;

    private static final List<String> PUBLIC_ENDPOINTS = List.of("/auth/register", "/auth/login", "/auth/confirm-email");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        if (PUBLIC_ENDPOINTS.contains(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        String sessionId = request.getHeader("Session-Id");

        if (sessionId != null && sessionService.validateSession(sessionId)) {
            logger.info("Valid session");
            UserSession session = sessionService.findBySessionId(sessionId);
            User user = session.getUser();

            logger.info("User roles: {}", user.getRoles()
                    .stream()
                    .map(role -> role.getType().toString())
                    .collect(Collectors.joining(", ")));

            var authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getType().name()))
                    .toList();

            var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            logger.info("Session expired or invalid");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Session expired or invalid");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
