package es.upm.tennis.tournament.manager.service;

import es.upm.tennis.tournament.manager.model.User;
import es.upm.tennis.tournament.manager.model.UserSession;
import es.upm.tennis.tournament.manager.repo.UserSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class UserSessionService {

    private static final Logger logger = LoggerFactory.getLogger(UserSessionService.class);

    @Autowired
    private UserSessionRepository userSessionRepository;

    private static final int SESSION_DURATION_MINUTES = 30;

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
        if (session != null && session.getExpirationDate().isAfter(Instant.now())) {
            // Refresh session expiration date
            session.setExpirationDate(Instant.now().plusSeconds(SESSION_DURATION_MINUTES * 60));
            userSessionRepository.save(session);
            return true;
        }
        return false;
    }

    public boolean invalidateSession(String sessionId) {
        UserSession session = findBySessionId(sessionId);
        if (session != null) {
            userSessionRepository.delete(session);
            return true;
        }
        return false;
    }

    public UserSession findBySessionId(String sessionId) {
        return userSessionRepository.findBySessionId(sessionId);
    }


}
