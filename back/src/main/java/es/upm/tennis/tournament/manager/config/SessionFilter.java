package es.upm.tennis.tournament.manager.config;

import es.upm.tennis.tournament.manager.model.UserSession;
import es.upm.tennis.tournament.manager.model.User;
import es.upm.tennis.tournament.manager.service.UserSessionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class SessionFilter extends OncePerRequestFilter {

    @Autowired
    private UserSessionService sessionService;

    private static final List<String> PUBLIC_ENDPOINTS = List.of("/auth/register", "/auth/login");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        if (PUBLIC_ENDPOINTS.contains(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        String sessionId = request.getHeader("Session-Id");

        if (sessionId != null && sessionService.validateSession(sessionId)) {
            UserSession session = sessionService.findBySessionId(sessionId);
            User user = session.getUser();

            var authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getType().name()))
                    .toList();

            var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Session expired or invalid");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
