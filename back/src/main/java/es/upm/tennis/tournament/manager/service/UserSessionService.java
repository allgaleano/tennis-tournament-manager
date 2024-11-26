package es.upm.tennis.tournament.manager.service;

import es.upm.tennis.tournament.manager.exceptions.CustomException;
import es.upm.tennis.tournament.manager.exceptions.ErrorCode;
import es.upm.tennis.tournament.manager.model.User;
import es.upm.tennis.tournament.manager.model.UserSession;
import es.upm.tennis.tournament.manager.repo.UserSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class UserSessionService {

    private static final Logger logger = LoggerFactory.getLogger(UserSessionService.class);

    @Autowired
    private UserSessionRepository userSessionRepository;

    private static final int SESSION_DURATION_MINUTES = 1440;

    public UserSession createSession(User user) { //

        UserSession session = userSessionRepository.findByUser(user);

        if (session == null) {
            session = new UserSession();
            session.setUser(user);
            session.setSessionId(UUID.randomUUID().toString());

        }
        session.setExpirationDate(Instant.now().plusSeconds(SESSION_DURATION_MINUTES * 60));
        userSessionRepository.save(session);

        return session;
    }

    public boolean validateSession(String sessionId) {
        UserSession session = userSessionRepository.findBySessionId(sessionId);
        if (session != null && session.getExpirationDate().isAfter(Instant.now()) && session.getUser().isEnabled()) {
            // Refresh session expiration date
            session.setExpirationDate(Instant.now().plusSeconds(SESSION_DURATION_MINUTES * 60));
            userSessionRepository.save(session);
            return true;
        }
        return false;
    }

    public void invalidateSession(String sessionId) {
        UserSession session = findBySessionId(sessionId);
        if (session == null) {
            throw new CustomException(
                    ErrorCode.INVALID_TOKEN,
                    "Sesión no encontrada"
            );
        }
        userSessionRepository.delete(session);
    }

    public UserSession findBySessionId(String sessionId) {
        return userSessionRepository.findBySessionId(sessionId);
    }

    public UserSession findByUser(User user) { return userSessionRepository.findByUser(user); }

    public UserSession getActiveSession(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            throw new CustomException(
                    ErrorCode.USER_NOT_FOUND,
                    "Usuario no encontrado"
            );
        }
        UserSession activeSession = userSessionRepository.findByUser(user);
        if (activeSession == null) {
            throw new CustomException(
                    ErrorCode.INVALID_TOKEN,
                    "Sesión inválida o expirada"
            );
        }
        return activeSession;
    }

}
