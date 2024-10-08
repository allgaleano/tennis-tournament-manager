package es.upm.tennis.tournament.manager.service;

import es.upm.tennis.tournament.manager.model.User;
import es.upm.tennis.tournament.manager.model.UserSession;
import es.upm.tennis.tournament.manager.repo.UserRepository;
import es.upm.tennis.tournament.manager.repo.UserSessionRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserSessionService {

    private static final Logger logger = LoggerFactory.getLogger(UserSessionService.class);

    @Autowired
    private UserSessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    private static final int SESSION_DURATION_MINUTES = 30;

    public String createSession(User user) { //

        UserSession session = user.getSession();

        if (session != null) {
            session.setExpirationDate(LocalDateTime.now().plusMinutes(SESSION_DURATION_MINUTES));
            sessionRepository.save(session);
        } else {
            session = new UserSession();
            session.setUser(user);
            session.setSessionId(UUID.randomUUID().toString());
            session.setExpirationDate(LocalDateTime.now().plusMinutes(SESSION_DURATION_MINUTES));

            user.setSession(session);
            sessionRepository.save(session);
        }

        return session.getSessionId();
    }

    public boolean validateSession(String sessionId) {
        UserSession session = sessionRepository.findBySessionId(sessionId);
        if (session != null && session.getExpirationDate().isAfter(LocalDateTime.now())) {
            // Refresh session expiration date
            session.setExpirationDate(LocalDateTime.now().plusMinutes(SESSION_DURATION_MINUTES));
            sessionRepository.save(session);
            return true;
        }
        return false;
    }

    public boolean invalidateSession(String sessionId) {
        logger.info("Attempting to invalidate session: {}", sessionId);
        UserSession session = sessionRepository.findBySessionId(sessionId);
        if (session != null) {
            logger.info("Session found: {}", sessionId);
            User user = session.getUser();
            if (user != null) {
                logger.info("User found: {}", user.getUsername());
                user.setSession(null);
                userRepository.save(user);
            }
            logger.info("Session deleted: {}", sessionId);
            return true;
        }
        logger.warn("Session not found: {}", sessionId);
        return false;
    }

    public UserSession findBySessionId(String sessionId) {
        return sessionRepository.findBySessionId(sessionId);
    }


}
